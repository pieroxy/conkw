package net.pieroxy.conkw.api.model.panels.atoms.model;

public abstract class TopLevelPanelElement extends DashboardDynamicElement {
    public abstract TopLevelPanelElementEnum getType();
    public abstract void initialize();
}
