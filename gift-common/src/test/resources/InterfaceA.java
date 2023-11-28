public interface InterfaceA {
    public void aa();

    public void ab();

    public void ca();

    public static class ClassB {
        public void ba() {
            System.out.println("ba");
        }
        public void bb() {
            System.out.println("bb");
        }
        public void cb() {
            System.out.println("cb");
        }

        public static interface ClassC {
            public void ca();

            public void cb();

            public void cc();
        }
    }
}