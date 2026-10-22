class TestUI extends UserInterface {

    private String[] inputs;
    private int[] ints;
    private int inputIndex = 0;
    private int intIndex = 0;

    TestUI(String[] inputs, int[] ints) { // This looks weird, varargs allows for variable size of arguments to be passed
        this.inputs = inputs;
        this.ints = ints;
    }

    @Override
    public String getInput() {
        return inputs[inputIndex++];
    }

    @Override
    public int getInt() {
        return ints[intIndex++];
    }

    @Override
    public void printMessage(String msg) {
        // ignore output for tests
    }
}