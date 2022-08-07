package net.pieroxy.conkw.api.model.panels;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.TypeScriptType;
import net.pieroxy.conkw.api.model.panels.atoms.model.TopLevelPanelElement;

import java.util.ArrayList;
import java.util.List;

@TypeScriptType
@CompiledJson
public class DashboardPanel {
  private String title;
  private String id;

  private List<TopLevelPanelElement> content;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<TopLevelPanelElement> getContent() {
    return content;
  }

  public void setContent(List<TopLevelPanelElement> content) {
    this.content = content;
  }

  public void addContent(TopLevelPanelElement element) {
    if (this.content == null) this.content = new ArrayList<>();
    this.content.add(element);
  }
}
