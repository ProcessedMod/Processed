package com.redcrafter07.processed.tileentity.helper;

import net.minecraft.nbt.CompoundNBT;

public class EnergyContainer {

    private int fillState;
    private int maxFillState;
    private String id;

    public EnergyContainer(int maxFillState, String id) {
        this.fillState = 0;
        this.maxFillState = maxFillState;
        this.id = id;
    }

    public void read(CompoundNBT nbt) {
        this.fillState = nbt.getInt("elecFillState"+this.id);

        if(this.fillState > this.maxFillState) {
            this.fillState = this.maxFillState;
        }
    }

    public CompoundNBT write(CompoundNBT nbt) {
        nbt.putInt("elecFillState"+this.id, this.fillState);

        return nbt;
    }

    /**
     * Increase the power of the EnergyContainer
     * @param increase how much power to put in
     * @return how much power is over the limit
     */
    public int increaseFillState(int increase) {
        this.fillState = this.fillState + increase;
        if (this.fillState > this.maxFillState) {
            int overload = this.fillState - this.maxFillState;
            this.fillState = this.maxFillState;
            return overload;
        }
        return 0;
    }

    /**
     * Removes power from the EnergyContainer
     * @param decrease how much power is wanted
     * @return how much power was taken out
     */
    public int decreaseFillState(int decrease) {
        int oldFillState = this.fillState;
        this.fillState = this.fillState - decrease;

        if(this.fillState < 0) {
            this.fillState = 0;
        }

        return (oldFillState-this.fillState);
    }

    public int getFillState() {
        if(this.fillState > this.maxFillState) this.fillState = this.maxFillState;
        return this.fillState;
    }
}
