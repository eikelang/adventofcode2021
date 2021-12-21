package day19;

import java.util.Objects;

class RelativePosition {

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RelativePosition that = (RelativePosition) o;
        return xDist == that.xDist && yDist == that.yDist && zDist == that.zDist;
    }

    @Override
    public int hashCode() {
        return Objects.hash(xDist, yDist, zDist);
    }

    private final int xDist;
    private final int yDist;
    private final int zDist;

    RelativePosition(final int xDist, final int yDist, final int zDist) {
        this.xDist = xDist;
        this.yDist = yDist;
        this.zDist = zDist;
    }

    RelativePosition rotateBy(final int xRot, final int yRot, final int zRot) {
        if (xRot == 0 && yRot == 0 && zRot == 0) {
            return this;
        }
        if (zRot == 1) {
            return new RelativePosition(yDist, -xDist, zDist).rotateBy(xRot, yRot, 0);
        }
        if (zRot == 2) {
            return new RelativePosition(-xDist, -yDist, zDist).rotateBy(xRot, yRot, 0);
        }
        if (zRot == 3) {
            return new RelativePosition(-yDist, xDist, zDist).rotateBy(xRot, yRot, 0);
        }
        if (yRot == 1) {
            return new RelativePosition(zDist, yDist, -xDist).rotateBy(xRot ,0, zRot);
        }
        if (yRot == 2) {
            return new RelativePosition(-xDist, yDist, -zDist).rotateBy(xRot ,0, zRot);
        }
        if (yRot == 3) {
            return new RelativePosition(-zDist, yDist, xDist).rotateBy(xRot ,0, zRot);
        }
        if (xRot == 1) {
            return new RelativePosition(xDist, -zDist, yDist).rotateBy(0 ,yRot, zRot);
        }
        if (xRot == 2) {
            return new RelativePosition(xDist, -yDist, -zDist).rotateBy(0 ,yRot, zRot);
        }
        if (xRot == 3) {
            return new RelativePosition(xDist, zDist, -yDist).rotateBy(0 ,yRot, zRot);
        }
        return null;
    }

    @Override
    public String toString() {
        return "(" + xDist +
                "," + yDist +
                "," + zDist +
                ')';
    }

    public RelativePosition relativeTo(final RelativePosition newOrigin) {
        return new RelativePosition(xDist - newOrigin.xDist, yDist - newOrigin.yDist, zDist - newOrigin.zDist);
    }

    public long squaredDistance() {
        return xDist*xDist + yDist*yDist + zDist*zDist;
    }
}
