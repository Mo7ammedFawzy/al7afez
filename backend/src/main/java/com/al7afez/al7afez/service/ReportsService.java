package com.al7afez.al7afez.service;

import com.al7afez.al7afez.dto.MistakeBreakdownResponse;
import com.al7afez.al7afez.dto.ReportDimensionResponse;
import com.al7afez.al7afez.dto.ReportSummaryResponse;
import com.al7afez.al7afez.dto.ReportsOverviewResponse;
import com.al7afez.al7afez.infra.SecurityService;
import com.al7afez.al7afez.model.entities.Level;
import com.al7afez.al7afez.model.entities.RecitationDocument;
import com.al7afez.al7afez.model.entities.RecitationGroup;
import com.al7afez.al7afez.model.details.RecitationMistakeLine;
import com.al7afez.al7afez.model.entities.Sheikh;
import com.al7afez.al7afez.model.entities.Student;
import com.al7afez.al7afez.repositories.GroupRepository;
import com.al7afez.al7afez.repositories.LevelRepository;
import com.al7afez.al7afez.repositories.RecitationDocumentRepository;
import com.al7afez.al7afez.repositories.StudentRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ReportsService {
    private final RecitationDocumentRepository recitationRepository;
    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;
    private final LevelRepository levelRepository;
    private final RecitationService recitationService;
    private final SecurityService securityService;

    public ReportsService(
            RecitationDocumentRepository recitationRepository,
            StudentRepository studentRepository,
            GroupRepository groupRepository,
            LevelRepository levelRepository,
            RecitationService recitationService,
            SecurityService securityService
    ) {
        this.recitationRepository = recitationRepository;
        this.studentRepository = studentRepository;
        this.groupRepository = groupRepository;
        this.levelRepository = levelRepository;
        this.recitationService = recitationService;
        this.securityService = securityService;
    }

    public ReportsOverviewResponse getOverview() {
        Optional<Sheikh> currentSheikh = securityService.getCurrentSheikh();
        List<RecitationDocument> recitations = recitationRepository.findAllDetailed();
        List<Student> students = currentSheikh
                .map(s -> studentRepository.findAllWithGroupBySheikh(s.getId()))
                .orElseGet(studentRepository::findAllWithGroup);
        List<RecitationGroup> groups = currentSheikh
                .map(s -> groupRepository.findAllWithDetailsBySheikh(s.getId()))
                .orElseGet(groupRepository::findAllWithDetails);
        List<Level> levels = levelRepository.findAll();

        long totalMistakes = recitations.stream()
                .flatMap(recitation -> recitation.getMistakes().stream())
                .map(RecitationMistakeLine::getCount)
                .filter(Objects::nonNull)
                .mapToLong(Integer::longValue)
                .sum();

        double averageGrade = recitations.stream()
                .map(RecitationDocument::getGrade)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);

        return new ReportsOverviewResponse(
                new ReportSummaryResponse(
                        students.size(),
                        groups.size(),
                        levels.size(),
                        recitations.size(),
                        totalMistakes,
                        round(averageGrade)
                ),
                buildMistakeBreakdown(recitations),
                buildStudentReport(students, recitations),
                buildGroupReport(groups, recitations),
                buildLevelReport(levels, recitations),
                recitationService.getRecent(8)
        );
    }

    private List<MistakeBreakdownResponse> buildMistakeBreakdown(List<RecitationDocument> recitations) {
        Map<Long, MistakeAggregate> aggregates = new LinkedHashMap<>();
        long total = 0;

        for (RecitationDocument recitation : recitations) {
            for (RecitationMistakeLine mistake : recitation.getMistakes()) {
                if (mistake.getMistakeType() == null || mistake.getCount() == null) {
                    continue;
                }
                total += mistake.getCount();
                aggregates.computeIfAbsent(
                        mistake.getMistakeType().getId(),
                        ignored -> new MistakeAggregate(mistake.getMistakeType().getId(), mistake.getMistakeType().getName())
                ).count += mistake.getCount();
            }
        }

        long denominator = total == 0 ? 1 : total;
        return aggregates.values().stream()
                .sorted(Comparator.comparingLong(MistakeAggregate::count).reversed().thenComparing(MistakeAggregate::name))
                .map(item -> new MistakeBreakdownResponse(item.id, item.name, item.count, round((item.count * 100.0) / denominator)))
                .toList();
    }

    private List<ReportDimensionResponse> buildStudentReport(List<Student> students, List<RecitationDocument> recitations) {
        Map<Long, List<RecitationDocument>> recitationsByStudent = recitations.stream()
                .filter(recitation -> recitation.getStudent() != null)
                .collect(Collectors.groupingBy(recitation -> recitation.getStudent().getId()));

        return students.stream()
                .sorted(Comparator.comparing(Student::getName, Comparator.nullsLast(String::compareToIgnoreCase)))
                .map(student -> {
                    List<RecitationDocument> items = recitationsByStudent.getOrDefault(student.getId(), List.of());
                    String secondary = student.getRecitationGroup() != null ? student.getRecitationGroup().getName() : "Unassigned";
                    return summarizeDimension(student.getId(), student.getName(), secondary, items);
                })
                .toList();
    }

    private List<ReportDimensionResponse> buildGroupReport(List<RecitationGroup> groups, List<RecitationDocument> recitations) {
        Map<Long, List<RecitationDocument>> recitationsByGroup = recitations.stream()
                .filter(recitation -> recitation.getStudent() != null && recitation.getStudent().getRecitationGroup() != null)
                .collect(Collectors.groupingBy(recitation -> recitation.getStudent().getRecitationGroup().getId()));

        return groups.stream()
                .sorted(Comparator.comparing(RecitationGroup::getName, Comparator.nullsLast(String::compareToIgnoreCase)))
                .map(group -> {
                    List<RecitationDocument> items = recitationsByGroup.getOrDefault(group.getId(), List.of());
                    String secondary = group.getLevel() != null ? group.getLevel().getName() : "No level";
                    return summarizeDimension(group.getId(), group.getName(), secondary, items);
                })
                .toList();
    }

    private List<ReportDimensionResponse> buildLevelReport(List<Level> levels, List<RecitationDocument> recitations) {
        Map<Long, List<RecitationDocument>> recitationsByLevel = recitations.stream()
                .filter(recitation -> recitation.getStudent() != null
                        && recitation.getStudent().getRecitationGroup() != null
                        && recitation.getStudent().getRecitationGroup().getLevel() != null)
                .collect(Collectors.groupingBy(recitation -> recitation.getStudent().getRecitationGroup().getLevel().getId()));

        return levels.stream()
                .sorted(Comparator.comparing(Level::getName, Comparator.nullsLast(String::compareToIgnoreCase)))
                .map(level -> summarizeDimension(level.getId(), level.getName(), rangeLabel(level), recitationsByLevel.getOrDefault(level.getId(), List.of())))
                .toList();
    }

    private ReportDimensionResponse summarizeDimension(Long id, String name, String secondaryLabel, List<RecitationDocument> recitations) {
        long mistakeCount = recitations.stream()
                .flatMap(recitation -> recitation.getMistakes().stream())
                .map(RecitationMistakeLine::getCount)
                .filter(Objects::nonNull)
                .mapToLong(Integer::longValue)
                .sum();
        double averageGrade = recitations.stream()
                .map(RecitationDocument::getGrade)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);

        LocalDate latestDate = recitations.stream()
                .map(RecitationDocument::getRecitationDate)
                .filter(Objects::nonNull)
                .max(LocalDate::compareTo)
                .orElse(null);

        return new ReportDimensionResponse(
                id,
                name,
                secondaryLabel,
                recitations.size(),
                mistakeCount,
                round(averageGrade),
                latestDate == null ? null : latestDate.toString(),
                topMistakes(recitations)
        );
    }

    private List<MistakeBreakdownResponse> topMistakes(List<RecitationDocument> recitations) {
        return buildMistakeBreakdown(recitations).stream().limit(3).toList();
    }

    private String rangeLabel(Level level) {
        List<String> segments = new ArrayList<>();
        if (level.getFromSurah() > 0 || level.getFromAya() > 0 || level.getToSurah() > 0 || level.getToAya() > 0) {
            segments.add(level.getFromSurah() + ":" + level.getFromAya() + " - " + level.getToSurah() + ":" + level.getToAya());
        }
        if (level.getNumberOfAyatPerSession() > 0) {
            segments.add(level.getNumberOfAyatPerSession() + " ayat/session");
        }
        return String.join(" • ", segments);
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private static final class MistakeAggregate {
        private final Long id;
        private final String name;
        private long count;

        private MistakeAggregate(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public long count() {
            return count;
        }

        public String name() {
            return name == null ? "" : name;
        }
    }
}
