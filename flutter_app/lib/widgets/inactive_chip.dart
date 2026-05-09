import 'package:flutter/material.dart';
import '../models/mistake_type.dart';

class InactiveChip extends StatelessWidget {
  final MistakeType type;
  final VoidCallback onTap;

  const InactiveChip({super.key, required this.type, required this.onTap});

  @override
  Widget build(BuildContext context) {
    final cs = Theme.of(context).colorScheme;
    return Material(
      color: Colors.transparent,
      shape: StadiumBorder(side: BorderSide(color: cs.outlineVariant)),
      clipBehavior: Clip.antiAlias,
      child: InkWell(
        onTap: onTap,
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 9),
          child: Text(
            type.name,
            style: TextStyle(fontSize: 13, color: cs.onSurface),
          ),
        ),
      ),
    );
  }
}
