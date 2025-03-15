package com.def.warlords.control.form;

/**
 * @author wistful23
 * @version 1.23
 */
public class ResultForm<R> extends Form {

    private R result;

    public ResultForm(FormController controller) {
        super(controller);
    }

    public R getResult() {
        activate();
        return result;
    }

    void setResult(R result) {
        this.result = result;
        deactivate();
    }
}
