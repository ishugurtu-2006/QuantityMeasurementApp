import java.util.Objects;

/**
 * UC6: Addition of quantities (same-unit and cross-unit).
 * Result is expressed in the unit of the first operand (unless overridden).
 *
 * Fixes:
 *  - File was test-only and referenced QuantityLength with getValue()/getUnit()/add()
 *    which didn't exist in prior UCs — full implementation added here.
 *  - Test was truncated; completed with all cases.
 *  - QuantityLength.add(a, b) added as static method (tests called it statically).
 *  - CENTIMETERS factor corrected: 1 cm = 1/30.48 ft = 0.032808... ft.
 */
public class UC6 {

    // ── LengthUnit ──────────────────────────────────────────────────────────
    public enum LengthUnit {
        FEET(1.0),
        INCHES(1.0 / 12.0),
        YARDS(3.0),
        CENTIMETERS(1.0 / 30.48); // exact: 1 ft = 30.48 cm

        private final double toFeetFactor;

        LengthUnit(double toFeetFactor) {
            this.toFeetFactor = toFeetFactor;
        }

        public double toFeet(double value)        { return value * toFeetFactor; }
        public double fromFeet(double feetValue)  { return feetValue / toFeetFactor; }
        public double toFeetFactor()              { return toFeetFactor; }
    }

    // ── QuantityLength ───────────────────────────────────────────────────────
    public static final class QuantityLength {
        private static final double EPSILON = 1e-9;

        private final double value;
        private final LengthUnit unit;

        public QuantityLength(double value, LengthUnit unit) {
            if (unit == null) throw new IllegalArgumentException("Unit cannot be null");
            this.value = value;
            this.unit  = unit;
        }

        public double getValue()    { return value; }
        public LengthUnit getUnit() { return unit; }

        /** Convert this quantity to the given target unit. */
        public QuantityLength convertTo(LengthUnit target) {
            if (target == null) throw new IllegalArgumentException("Target unit cannot be null");
            double newValue = target.fromFeet(unit.toFeet(value));
            return new QuantityLength(newValue, target);
        }

        /**
         * Instance add — result expressed in THIS quantity's unit.
         * (Convenience; internally delegates to static add.)
         */
        public QuantityLength add(QuantityLength other) {
            return add(this, other);
        }

        /**
         * Static add — result expressed in the unit of the first operand.
         * Tests call it as QuantityLength.add(a, b).
         */
        public static QuantityLength add(QuantityLength a, QuantityLength b) {
            if (a == null || b == null) throw new IllegalArgumentException("Operands cannot be null");
            double sumFeet = a.unit.toFeet(a.value) + b.unit.toFeet(b.value);
            double resultValue = a.unit.fromFeet(sumFeet);
            return new QuantityLength(resultValue, a.unit);
        }

        /** Static add with explicit target unit (used by UC7). */
        public static QuantityLength add(QuantityLength a, QuantityLength b, LengthUnit targetUnit) {
            if (a == null || b == null) throw new IllegalArgumentException("Operands cannot be null");
            if (targetUnit == null) throw new IllegalArgumentException("Target unit cannot be null");
            double sumFeet   = a.unit.toFeet(a.value) + b.unit.toFeet(b.value);
            double resultValue = targetUnit.fromFeet(sumFeet);
            return new QuantityLength(resultValue, targetUnit);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof QuantityLength q)) return false;
            return Math.abs(unit.toFeet(value) - q.unit.toFeet(q.value)) <= EPSILON;
        }

        @Override
        public int hashCode() {
            long key = Math.round(unit.toFeet(value) * 1e9);
            return Objects.hash(key);
        }

        @Override
        public String toString() {
            return value + " " + unit;
        }
    }

    // ── Demo main (mirrors the test assertions) ──────────────────────────────
    public static void main(String[] args) {
        final double EPS = 1e-9;

        // Same-unit: FEET + FEET = 3 ft
        QuantityLength r1 = QuantityLength.add(
                new QuantityLength(1.0, LengthUnit.FEET),
                new QuantityLength(2.0, LengthUnit.FEET));
        System.out.println("1 ft + 2 ft        = " + r1 + "  (expect 3.0 FEET)");

        // Same-unit: INCHES + INCHES = 12 inches
        QuantityLength r2 = QuantityLength.add(
                new QuantityLength(6.0, LengthUnit.INCHES),
                new QuantityLength(6.0, LengthUnit.INCHES));
        System.out.println("6 in + 6 in        = " + r2 + " (expect 12.0 INCHES)");

        // Cross-unit: FEET + INCHES -> FEET (1 ft + 12 in = 2 ft)
        QuantityLength r3 = QuantityLength.add(
                new QuantityLength(1.0, LengthUnit.FEET),
                new QuantityLength(12.0, LengthUnit.INCHES));
        System.out.println("1 ft + 12 in       = " + r3 + "  (expect 2.0 FEET)");

        // Cross-unit: INCHES + FEET -> INCHES (12 in + 1 ft = 24 in)
        QuantityLength r4 = QuantityLength.add(
                new QuantityLength(12.0, LengthUnit.INCHES),
                new QuantityLength(1.0, LengthUnit.FEET));
        System.out.println("12 in + 1 ft       = " + r4 + " (expect 24.0 INCHES)");

        // Cross-unit: CM + INCHES -> CM (2.54 cm + 1 in = 5.08 cm)
        QuantityLength r5 = QuantityLength.add(
                new QuantityLength(2.54, LengthUnit.CENTIMETERS),
                new QuantityLength(1.0, LengthUnit.INCHES));
        System.out.println("2.54 cm + 1 in     = " + r5 + " (expect ~5.08 CENTIMETERS)");

        // Identity: x + 0 = x
        QuantityLength r6 = QuantityLength.add(
                new QuantityLength(5.0, LengthUnit.FEET),
                new QuantityLength(0.0, LengthUnit.FEET));
        System.out.println("5 ft + 0 ft        = " + r6 + "  (expect 5.0 FEET)");
    }
}
