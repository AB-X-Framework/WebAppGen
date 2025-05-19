package org.abx.webappgen.persistence;

import org.abx.webappgen.persistence.dao.MethodSpecRepository;
import org.abx.webappgen.persistence.model.MethodSpec;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.abx.webappgen.persistence.PageModel.elementHashCode;


@Component
public class MethodModel {
    @Autowired
    public MethodSpecRepository methodSpecRepository;

    public JSONObject getMethodSpec(String methodName) {
        JSONObject methodSpecJson = new JSONObject();
        MethodSpec spec =
                methodSpecRepository.findByMethodSpecId(elementHashCode(methodName));
        if (spec != null) {
           return null;
        }
        methodSpecJson.put("type", spec.type);
        methodSpecJson.put("methodJS", spec.methodJS);
        methodSpecJson.put("outputName", spec.outputName);
        methodSpecJson.put("role", spec.role);
        return methodSpecJson;
    }
}
