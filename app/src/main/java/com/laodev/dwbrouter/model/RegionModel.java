package com.laodev.dwbrouter.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RegionModel implements Serializable {
    public String title = "";
    public String color = "";
    public List<String> postal = new ArrayList<>();
}
