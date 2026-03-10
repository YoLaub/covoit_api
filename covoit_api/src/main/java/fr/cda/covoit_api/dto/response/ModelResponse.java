package fr.cda.covoit_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelResponse {
    private Integer id;
    private String label;
    private Integer brandId;
    private String brandLabel;
}