package org.objectrepository.validation;

/**
 * Created with IntelliJ IDEA.
 * User: cro
 * Date: 18-7-12
 * Time: 10:47
 * To change this template use File | Settings | File Templates.
 */
public class objectNumber {

    private int objectNumber;
    private int volgNumber;
    private int lineNumber;


    public objectNumber(){

        this.objectNumber = 0;
        this.volgNumber = 0;
        this.lineNumber = 0;

    }

    public objectNumber(int objectNumber, int volgNumber, int lineNumber){

        this.objectNumber = objectNumber;
        this.volgNumber = volgNumber;
        this.lineNumber = lineNumber;

    }

    public void setObjectNumber(int objectNumber){

        this.objectNumber = objectNumber;

    }

    public void setVolgNumber(int volgNumber){

        this.volgNumber = volgNumber;

    }

    public int getObjectNumber(){

        return this.objectNumber;

    }

    public int getVolgNumber(){

        return this.volgNumber;

    }


    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String toString(){

        return this.objectNumber + ":" + this.volgNumber;

    }

}
