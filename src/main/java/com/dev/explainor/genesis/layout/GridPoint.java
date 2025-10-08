package com.dev.explainor.genesis.layout;

public final class GridPoint {
    public final int x;
    public final int y;

    public GridPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GridPoint that = (GridPoint) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }
}


