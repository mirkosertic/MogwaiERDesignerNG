/*
 * ValidatorResult.java
 *
 * Created on July 9, 2002, 10:44 PM
 */

package de.erdesignerng.test.mimer;

/**
 *
 * @author  olle
 */
public class ValidatorResult {
    
    /** Holds value of property text. */
    private String data;
    
    /** Holds value of property standard. */
    private int standard;
    
    /** Creates a new instance of ValidatorResult */
    public ValidatorResult() {
    }
    
    /** Getter for property text.
     * @return Value of property text.
     */
    public String getData() {
        return this.data;
    }
    
    /** Setter for property text.
     * @param text New value of property text.
     */
    public void setData(String data) {
        this.data = data;
    }
    
    /** Getter for property standard.
     * @return Value of property standard.
     */
    public int getStandard() {
        return this.standard;
    }
    
    /** Setter for property standard.
     * @param standard New value of property standard.
     */
    public void setStandard(int standard) {
        this.standard = standard;
    }
    
    @Override
    public String toString() {
        return "standard = " + this.standard +
               " (0 = not standard, 1 = Core, 2 = Core plus extensions)\n" +
               "\ndata = " + this.data;
    }
    
}