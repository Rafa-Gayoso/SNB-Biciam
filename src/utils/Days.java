package utils;

public enum Days {
    LUNES{
        @Override
        public String toString() {
            return "Lunes";
        }
    },
    MARTES{
        @Override
        public String toString() {
            return "Martes";
        }
    },
    MIERCOLES{
        @Override
        public String toString() {
            return "Mi\u00e9rcoles";
        }
    },
    JUEVES{
        @Override
        public String toString() {
            return "Jueves";
        }
    },
    VIERNES{
        @Override
        public String toString() {
            return "Viernes";
        }
    },
    SABADO{
        @Override
        public String toString() {
            return "S\u00e1bado";
        }
    },
    DOMINGO{
        @Override
        public String toString() {
            return "Domingo";
        }
    }
}
