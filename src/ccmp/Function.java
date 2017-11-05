/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccmp;

import java.util.ArrayList;
import java.util.Enumeration;
import java.io.File;
import java.util.Scanner;

/**
 *
 * @author liem
 */
public class Function {

    int id;
    int requiredResource = 1;

    public Function() {
        id = -1;
    }

    public Function(Function f) {
        id = f.getId();
    }

    public Function(int fid) {
        id = fid;
    }

    public int getRequireResource() {
        return requiredResource;
    }

    public void setRequireResource(int rs) {
        requiredResource = rs;
    }

    public int getId() {
        return id;
    }

    public void setId(int i) {
        id = i;
    }

}
