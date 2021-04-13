package com.groupProject.groupProject.controlles;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class GradeRequest {
    @Min(value = 0,message = "Min value is 0")
    @Max( value = 10,message = "Max value is 10")
    private double value;
}
