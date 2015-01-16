package pt.ipleiria.estg.meicm.iaupss.estgparking.utils;

public class Rectangle {

    private Dot dotA;
    private Dot dotB;
    private Dot dotC;
    private Dot dotD;

    public Rectangle() {

    }

    public boolean dotIsInside(Dot dotP) {

        return  (checkAB(dotP) && checkBC(dotP) && checkCD(dotP) && checkDA(dotP) ? true : false);
    }

    private boolean checkAB(Dot dotP) {

        double d = (dotA.getLat() - dotB.getLat()) * (dotP.getLng() - dotB.getLng()) -
                (dotA.getLng() - dotB.getLng()) * (dotP.getLat() - dotB.getLat());

        return  (d <= 0 ? true : false);
    }

    private boolean checkBC(Dot dotP) {

        double d = (dotB.getLat() - dotC.getLat()) * (dotP.getLng() - dotC.getLng()) -
                (dotB.getLng() - dotC.getLng()) * (dotP.getLat() - dotC.getLat());

        return  (d <= 0 ? true : false);
    }

    private boolean checkCD(Dot dotP) {

        double d = (dotC.getLat() - dotD.getLat()) * (dotP.getLng() - dotD.getLng()) -
                (dotC.getLng() - dotD.getLng()) * (dotP.getLat() - dotD.getLat());

        return  (d <= 0 ? true : false);
    }

    private boolean checkDA(Dot dotP) {

        double d = (dotD.getLat() - dotA.getLat()) * (dotP.getLng() - dotA.getLng()) -
                (dotD.getLng() - dotA.getLng()) * (dotP.getLat() - dotA.getLat());

        return  (d <= 0 ? true : false);
    }

    public void setDotA(Dot dotA) {
        this.dotA = dotA;
    }

    public void setDotB(Dot dotB) {
        this.dotB = dotB;
    }

    public void setDotC(Dot dotC) {
        this.dotC = dotC;
    }

    public void setDotD(Dot dotD) {
        this.dotD = dotD;
    }
}
