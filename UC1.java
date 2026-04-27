/**
 * UC1: Feet class with proper value-based equality.
 * Two Feet objects with the same numeric value are considered equal.
 *
 * Fix: renamed outer class from QuantityMeasurementApp -> UC1
 *      (file name must match public class name in Java).
 */
public class UC1 {

    public static class Feet {
        private final double value;

        public Feet(double value) {
            this.value = value;
        }

        public double getValue() {
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            // Reflexive
            if (this == obj) return true;

            // Null check
            if (obj == null) return false;

            // Type safety: only equal to Feet
            if (getClass() != obj.getClass()) return false;

            Feet other = (Feet) obj;

            // Floating-point comparison
            return Double.compare(this.value, other.value) == 0;
        }

        @Override
        public int hashCode() {
            // Keep hashCode consistent with equals
            return Double.hashCode(value);
        }

        @Override
        public String toString() {
            return value + " feet";
        }
    }

    public static void main(String[] args) {
        Feet a = new Feet(1.0);
        Feet b = new Feet(1.0);
        Feet c = new Feet(2.0);

        System.out.println("1.0 ft == 1.0 ft : " + a.equals(b)); // true
        System.out.println("1.0 ft == 2.0 ft : " + a.equals(c)); // false
    }
}
