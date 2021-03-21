package net.pieroxy.conkw.webapp.grabbers.openweathermap;

import com.dslplatform.json.CompiledJson;

@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
public class WeatherAlert {
  String sender_name;
  String event;
  double start;
  double end;
  String description;

  public String getSender_name() {
    return sender_name;
  }

  public void setSender_name(String sender_name) {
    this.sender_name = sender_name;
  }

  public String getEvent() {
    return event;
  }

  public void setEvent(String event) {
    this.event = event;
  }

  public double getStart() {
    return start;
  }

  public void setStart(double start) {
    this.start = start;
  }

  public double getEnd() {
    return end;
  }

  public void setEnd(double end) {
    this.end = end;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}