import java.util.Objects;

/**
 * UC9: Weight measurement — KILOGRAM, GRAM, POUND.
 * Supports equality, conversion, and addition (same-unit and cross-unit).
 *
 * Fixes:
 *  - Heading was "#U9" (typo); corrected to UC9.
 *  - nearlyEqual() method body was truncated — completed.
 *  - Missing closing brace for QuantityWeight class added.
 *  - Missing "return false" at end of equals() added.
 *  - Class renamed/restructured so file compiles as UC9.java.
 */
public class UC9 {

    // ── WeightUnit ───────────────────────────────────────────────────────────
    public enum WeightUnit {
        KILOGRAM("KG",  1.0),
        GRAM    ("G",   1.0 / 1000.0),
        POUND   ("LB",  0.45359237); // 1 lb = 0.45359237 kg

        private final String symbol;
        private final double toKilogramFactor; // value_in_this_unit * factor = kg

        WeightUnit(String symbol, double toKilogramFactor) {
            this.symbol           = symbol;
            this.toKilogramFactor = toKilogramFactor;
        }

        public String getSymbol() {
            return symbol;
        }

        /** Convert a value in THIS unit to kilograms (base unit). */
        public double convertToBaseUnit(double value) {
            return value * toKilogramFactor;
        }

        /** Convert a value in kilograms back to THIS unit. */
        public double convertFromBaseUnit(double baseUnitValue) {
            return baseUnitValue / toKilogramFactor;
        }

        /** Convenience: convert a value directly from THIS unit to targetUnit. */
        public double convert(double value, WeightUnit targetUnit) {
            if (targetUnit == null) throw new IllegalArgumentException("targetUnit must not be null");
            double kg = convertToBaseUnit(value);
            return targetUnit.convertFromBaseUnit(kg);
        }
    }

    // ── QuantityWeight ───────────────────────────────────────────────────────
    public static final class QuantityWeight {
        private static final double EPSILON = 1e-9;

        private final double value;
        private final WeightUnit unit;

        public QuantityWeight(double value, WeightUnit unit) {
            if (unit == null) throw new IllegalArgumentException("unit must not be null");
            this.value = value;
            this.unit  = unit;
        }

        public double getValue()      { return value; }
        public WeightUnit getUnit()   { return unit; }

        /** Convert this quantity to the given target unit. */
        public QuantityWeight convertTo(WeightUnit targetUnit) {
            if (targetUnit == null) throw new IllegalArgumentException("targetUnit must not be null");
            if (targetUnit == unit) return new QuantityWeight(value, unit);
            double targetValue = unit.convert(value, targetUnit);
            return new QuantityWeight(targetValue, targetUnit);
        }

        /** Add — result expressed in THIS quantity's unit (implicit target). */
        public QuantityWeight add(QuantityWeight other) {
            if (other == null) throw new IllegalArgumentException("other must not be null");
            double otherInThisUnit = other.unit.convert(other.value, this.unit);
            return new QuantityWeight(this.value + otherInThisUnit, this.unit);
        }

        /** Add — result expressed in the given target unit (explicit target). */
        public QuantityWeight add(QuantityWeight other, WeightUnit targetUnit) {
            if (other == null)      throw new IllegalArgumentException("other must not be null");
            if (targetUnit == null) throw new IllegalArgumentException("targetUnit must not be null");
            double sumInKg      = this.unit.convertToBaseUnit(this.value)
                                + other.unit.convertToBaseUnit(other.value);
            double sumInTarget  = targetUnit.convertFromBaseUnit(sumInKg);
            return new QuantityWeight(sumInTarget, targetUnit);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof QuantityWeight)) return false;     // FIX: was missing return false
            QuantityWeight other = (QuantityWeight) obj;
            double thisKg  = this.unit.convertToBaseUnit(this.value);
            double otherKg = other.unit.convertToBaseUnit(other.value);
            return nearlyEqual(thisKg, otherKg);
        }

        @Override
        public int hashCode() {
            // Quantise base-unit value so hash is stable for "equal within EPSILON" pairs.
            double kg       = unit.convertToBaseUnit(value);
            long quantized  = Math.round(kg / EPSILON);
            return Objects.hash(quantized);
        }

        @Override
        public String toString() {
            return value + " " + unit.getSymbol();
        }

        /**
         * FIX: method body was completely truncated in the original.
         * Proper NaN/Infinity-aware floating-point near-equality check.
         */
        private static boolean nearlyEqual(double a, double b) {
            if (Double.isNaN(a) || Double.isNaN(b))           return false;
            if (Double.isInfinite(a) || Double.isInfinite(b)) return a == b;
            return Math.abs(a - b) <= EPSILON;
        }
    }

    // ── Demo main ────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        // 1 kg == 1000 g
        System.out.println("1 kg == 1000 g  : " +
                new QuantityWeight(1.0, WeightUnit.KILOGRAM)
                        .equals(new QuantityWeight(1000.0, WeightUnit.GRAM)));   // true

        // 1 lb == 453.59237 g
        QuantityWeight oneLb = new QuantityWeight(1.0, WeightUnit.POUND);
        QuantityWeight grams = oneLb.convertTo(WeightUnit.GRAM);
        System.out.printf("1 lb in grams   : %.5f g  (expect 453.59237)%n", grams.getValue());

        // Add: 500 g + 500 g = 1 kg (expressed in kg)
        QuantityWeight sum = new QuantityWeight(500.0, WeightUnit.GRAM)
                .add(new QuantityWeight(500.0, WeightUnit.GRAM), WeightUnit.KILOGRAM);
        System.out.println("500 g + 500 g -> KG: " + sum);                     // 1.0 KG

        // Add same-unit: 1 kg + 1 kg = 2 kg
        QuantityWeight sum2 = new QuantityWeight(1.0, WeightUnit.KILOGRAM)
                .add(new QuantityWeight(1.0, WeightUnit.KILOGRAM));
        System.out.println("1 kg + 1 kg     : " + sum2);                       // 2.0 KG

        // nearlyEqual guard: NaN should not equal anything
        QuantityWeight nanWeight = new QuantityWeight(Double.NaN, WeightUnit.KILOGRAM);
        System.out.println("NaN kg == NaN kg: " + nanWeight.equals(nanWeight)); // false (NaN ≠ NaN)
    }
}
