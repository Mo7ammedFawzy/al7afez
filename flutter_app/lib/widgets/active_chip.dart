import 'package:flutter/material.dart';
import '../models/mistake_line.dart';
import '../models/mistake_type.dart';

class ActiveChip extends StatelessWidget {
  final MistakeType type;
  final MistakeLine mistake;
  final VoidCallback onIncrement;
  final VoidCallback onDecrement;

  const ActiveChip({
    super.key,
    required this.type,
    required this.mistake,
    required this.onIncrement,
    required this.onDecrement,
  });

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;

    return Material(
      color: cs.primary,
      shape: const StadiumBorder(),
      clipBehavior: Clip.antiAlias,
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          // − button
          InkWell(
            onTap: onDecrement,
            child: const Padding(
              padding: EdgeInsetsDirectional.fromSTEB(12, 9, 6, 9),
              child: Icon(Icons.remove_rounded, size: 16, color: Colors.white),
            ),
          ),

          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 4, vertical: 9),
            child: Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                Text(
                  type.name,
                  style: const TextStyle(
                    fontSize: 13,
                    fontWeight: FontWeight.w600,
                    color: Colors.white,
                  ),
                ),
                const SizedBox(width: 8),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 7, vertical: 1),
                  decoration: BoxDecoration(
                    color: Colors.white.withValues(alpha: 0.25),
                    borderRadius: BorderRadius.circular(20),
                  ),
                  child: Text(
                    '${mistake.count}',
                    style: const TextStyle(
                      fontSize: 12,
                      fontWeight: FontWeight.bold,
                      color: Colors.white,
                    ),
                  ),
                ),
              ],
            ),
          ),

          // + button
          InkWell(
            onTap: onIncrement,
            child: const Padding(
              padding: EdgeInsetsDirectional.fromSTEB(6, 9, 12, 9),
              child: Icon(Icons.add_rounded, size: 16, color: Colors.white),
            ),
          ),
        ],
      ),
    );
  }
}
