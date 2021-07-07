package utils;

public enum Days {
    LUNES{
        @Override
        public String toString() {
            return "Lu";
        }
    },
    MARTES{
        @Override
        public String toString() {
            return "Ma";
        }
    },
    MIERCOLES{
        @Override
        public String toString() {
            return "Mi";
        }
    },
    JUEVES{
        @Override
        public String toString() {
            return "Ju";
        }
    },
    VIERNES{
        @Override
        public String toString() {
            return "Vi";
        }
    },
    SABADO{
        @Override
        public String toString() {
            return "S\u00e1";
        }
    },
    DOMINGO{
        @Override
        public String toString() {
            return "Do";
        }
    }
}
