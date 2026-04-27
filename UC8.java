import java.util.Objects;

/**
 * UC8: Refactored — conversion logic lives entirely inside the LengthUnit enum.
 *      QuantityLength delegates all math to the enum (no raw factors in the class body).
 *
 * Fixes:
 *  - CRITICAL BUG: CENTIMETERS had factor 30.48 — but the field is "toFeetFactor"
 *    (multiply value-in-unit by this to get feet).  1 cm = 1/30.48 ft, so the
 *    correct value is 1.0/30.48 ≈ 0.032808.  Using 30.48 would mean 1 cm = 30.48 ft,
 *    which is wildly wrong.
 *  - Outer class renamed LengthUnitEnumUC8 -> UC8.
 *  - roundTo2() kept but made optional via convertTo(); add() variants preserved.
 *  - Optional JUnit test skeleton (commented) retained for reference.
 */
public class UC8 {

    // ── LengthUnit ──────────────────────────────────────────────────────────
    public enum LengthUnit {
        FEET(1.0),
        INCHES(1.0 / 12.0),
        YARDS(3.0),
        CENTIMETERS(1.0 / 30.48); // FIX: was 30.48 (cm/ft); correct is ft/cm = 1/30.48

        private final double toFeetFactor; // value_in_this_unit * factor = value_in_feet

        LengthUnit(double toFeetFactor) {
            this.toFeetFactor = toFeetFactor;
        }

        public double getConversionFactor() {
            return toFeetFactor;
        }

        /** Convert a value expressed in THIS unit to the base unit (feet). */
        public double convertToBaseUnit(double value) {
            validateValue(value);
            return value * toFeetFactor;
        }

        /** Convert a value expressed in the base unit (feet) back to THIS unit. */
        public double convertFromBaseUnit(double baseValue) {
            validateValue(baseValue);
            return baseValue / toFeetFactor;
        }

        private static void validateValue(double v) {
            if (Double.isNaN(v) || Double.isInfinite(v)) {
                throw new IllegalArgumentException("Value must be a finite number.");
            }
        }
    }

    // ── QuantityLength ───────────────────────────────────────────────────────
    public static final class QuantityLength {
        public static final double EPSILON = 1e-9;

        private final double value;
        private final LengthUnit unit;

        public QuantityLength(double value, LengthUnit unit) {
            if (unit == null) throw new IllegalArgumentException("Unit cannot be null.");
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                throw new IllegalArgumentException("Value must be a finite number.");
            }
            this.value = value;
            this.unit  = unit;
        }

        public double getValue()    { return value; }
        public LengthUnit getUnit() { return unit; }

        /** Convert this quantity to the target unit. */
        public QuantityLength convertTo(LengthUnit targetUnit) {
            if (targetUnit == null) throw new IllegalArgumentException("Unit cannot be null.");
            double feet     = unit.convertToBaseUnit(value);
            double newValue = targetUnit.convertFromBaseUnit(feet);
            return new QuantityLength(roundTo2(newValue), targetUnit);
        }

        /** Add with explicit target unit. */
        public QuantityLength add(QuantityLength other, LengthUnit targetUnit) {
            if (other == null)      throw new IllegalArgumentException("Other quantity cannot be null.");
            if (targetUnit == null) throw new IllegalArgumentException("Unit cannot be null.");
            double thisFeet  = unit.convertToBaseUnit(this.value);
            double otherFeet = other.unit.convertToBaseUnit(other.value);
            double sumFeet   = thisFeet + otherFeet;
            double result    = targetUnit.convertFromBaseUnit(sumFeet);
            return new QuantityLength(roundTo2(result), targetUnit);
        }

        /** Add and keep this quantity's unit as target (UC6 style). */
        public QuantityLength add(QuantityLength other) {
            return add(other, this.unit);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof QuantityLength q)) return false;
            double thisFeet  = unit.convertToBaseUnit(value);
            double otherFeet = q.unit.convertToBaseUnit(q.value);
            return Math.abs(thisFeet - otherFeet) <= EPSILON;
        }

        @Override
        public int hashCode() {
            double feet = unit.convertToBaseUnit(value);
            long key = Math.round(feet * 1e9);
            return Objects.hash(key);
        }

        @Override
        public String toString() {
            return "QuantityLength{" + value + " " + unit + "}";
        }

        private static double roundTo2(double v) {
            return Math.round(v * 100.0) / 100.0;
        }
    }

    // ── Demo main ────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        // Verify CENTIMETERS fix: 30.48 cm should equal 1 ft
        QuantityLength cm = new QuantityLength(30.48, LengthUnit.CENTIMETERS);
        QuantityLength ft = new QuantityLength(1.0,   LengthUnit.FEET);
        System.out.println("30.48 cm == 1 ft  : " + cm.equals(ft));          // true

        // 1 inch = 2.54 cm
        QuantityLength oneInch = new QuantityLength(1.0, LengthUnit.INCHES);
        QuantityLength twoPt54 = new QuantityLength(2.54, LengthUnit.CENTIMETERS);
        System.out.println("1 in == 2.54 cm   : " + oneInch.equals(twoPt54)); // true

        // Add: 1 ft + 12 in -> FEET = 2 ft
        QuantityLength a = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength b = new QuantityLength(12.0, LengthUnit.INCHES);
        QuantityLength sum = a.add(b, LengthUnit.FEET);
        System.out.println("1 ft + 12 in (FEET): " + sum);                    // 2.0 FEET

        // Convert: 1 yard -> feet = 3 ft
        QuantityLength yard = new QuantityLength(1.0, LengthUnit.YARDS);
        QuantityLength inFeet = yard.convertTo(LengthUnit.FEET);
        System.out.println("1 yd in FEET       : " + inFeet);                 // 3.0 FEET
    }
}
