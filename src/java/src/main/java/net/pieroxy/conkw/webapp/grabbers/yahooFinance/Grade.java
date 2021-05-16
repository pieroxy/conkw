package net.pieroxy.conkw.webapp.grabbers.yahooFinance;

public class Grade {
  String firm;
  String toGrade;
  String fromGrade;
  String action;
  double epochGradeDate;


  public double getEpochGradeDate() {
    return epochGradeDate;
  }

  public void setEpochGradeDate(double epochGradeDate) {
    this.epochGradeDate = epochGradeDate;
  }

  public String getFirm() {
    return firm;
  }

  public void setFirm(String firm) {
    this.firm = firm;
  }

  public String getToGrade() {
    return toGrade;
  }

  public void setToGrade(String toGrade) {
    this.toGrade = toGrade;
  }

  public String getFromGrade() {
    return fromGrade;
  }

  public void setFromGrade(String fromGrade) {
    this.fromGrade = fromGrade;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }
}
