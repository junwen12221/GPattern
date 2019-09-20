package cn.lightfish;

public abstract class Item {
    String pettern;
    String code;
    Instruction instruction;

    public Item(String pettern, String code) {
        this.pettern = pettern;
        this.code = code;
    }

    public String getPettern() {
        return pettern;
    }

    public String getCode() {
        return code;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public void setInstruction(Instruction instruction) {
        this.instruction = instruction;
    }
}
