package org.pahappa.systems.requisitionapp.views.utils;

import org.pahappa.systems.requisitionapp.models.BudgetLineCategory;
import org.pahappa.systems.requisitionapp.services.BudgetLineCategoryService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(forClass = BudgetLineCategory.class)
public class BudgetLineCategoryConverter implements Converter {

    private BudgetLineCategoryService getBudgetLineCategoryService() {
        WebApplicationContext ctx = FacesContextUtils.getWebApplicationContext(FacesContext.getCurrentInstance());
        assert ctx != null;
        return ctx.getBean(BudgetLineCategoryService.class);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return getBudgetLineCategoryService().getBudgetLineCategory(Integer.parseInt(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof BudgetLineCategory) {
            BudgetLineCategory category = (BudgetLineCategory) value;
            return String.valueOf(category.getId());
        }
        return "";
    }
}