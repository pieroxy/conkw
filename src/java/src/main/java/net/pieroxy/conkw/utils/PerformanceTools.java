package net.pieroxy.conkw.utils;

import net.pieroxy.conkw.webapp.grabbers.procgrabber.ProcessStat;

import java.io.*;

public class PerformanceTools {

  public static void parseLongInString(String line, int[]positions, long[]result) {
    int ct=0;
    int curpos=0;
    boolean parsing = false;
    for (int i=0 ; i<line.length() ; i++) {
      char c = line.charAt(i);
      if (c == ' ') {
        ct++;
        if (parsing) {
          curpos++;
          if (curpos==positions.length) return;
          parsing = false;
        }
      } else {
        if (ct == positions[curpos]) {
          parsing = true;
          result[curpos] = result[curpos]*10 + Character.getNumericValue(c);
        }
      }
    }
  }

  public static void parseDoubleInFileFirstLine(String file, byte[]babuffer, int numNumbers, double[]result) {
    try (FileInputStream fis = new FileInputStream(file)) {
      int pos=0,read=0;
      while (read!=-1) {
        pos += read;
        read = fis.read(babuffer, pos, babuffer.length-pos);
      }

      int numberIndex=0;
      double currentResult=0;
      int currentDecimalsFactor=1;
      boolean decimalPart=false;
      for (int i=0 ; i<pos ; i++) {
        byte b = babuffer[i];
        if (b==' ') {
          result[numberIndex] = currentResult / currentDecimalsFactor;
          currentResult=0;
          currentDecimalsFactor=1;
          decimalPart=false;
          numberIndex++;
          if (numberIndex>=numNumbers) return;
        } else if (b=='.') {
          decimalPart = true;
        } else {
          if (decimalPart) currentDecimalsFactor*=10;
          currentResult = currentResult*10 + b-48;
        }
      }
      result[numberIndex] = currentResult / currentDecimalsFactor;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void parseLongsInFileFirstLine(String file, byte[]babuffer, int[]positions, long[]result) {
    try (FileInputStream fis = new FileInputStream(file)) {
      int pos=0,read=0;
      while (read!=-1) {
        pos += read;
        read = fis.read(babuffer, pos, babuffer.length-pos);
      }

      int posIndex=0;
      int targetWordNumber = positions[posIndex];
      int wordNum=-1;
      long extracted=0;
      boolean extracting=false;
      boolean lastSpace = true;
      for (int i=0 ; i<pos ; i++) {
        byte b = babuffer[i];
        if (b==' ') {
          if (extracting) {
            extracting=false;
            result[posIndex]=extracted;
            extracted = 0;
            posIndex++;
            if (posIndex == positions.length) return;
            targetWordNumber = positions[posIndex];
          }
          if (wordNum > 6) break;
          lastSpace=true;
          continue;
        } else {
          if (lastSpace) {
            wordNum++;
            lastSpace=false;
          }
          if (wordNum == targetWordNumber) {
            extracting=true;
            extracted = extracted*10 + b-48;
          }
        }
      }
      if (extracting) {
        result[posIndex]=extracted;
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static ProcessStat parseProcessStatFile(File f, long pid, byte[]babuffer) {
    if (!f.exists()) return null;
    try (FileInputStream fis = new FileInputStream(f)) {
      int pos=0,read=0;
      while (read!=-1) {
        pos += read;
        read = fis.read(babuffer, pos, babuffer.length-pos);
      }

      int lastParenthesis = pos-1;
      int firstParenthesis = 0;
      while (babuffer[lastParenthesis--]!=')');
      while (babuffer[firstParenthesis++]!='(');
      int npos = firstParenthesis;
      int nlen = lastParenthesis-firstParenthesis+1;

      long utime=0,stime=0,mem=0;
      pos = lastParenthesis+3;
      read = 2;
      while (true) {
        byte b = babuffer[pos++];
        if (b==32) {
          read++;
          if (read==14) break;
        } else if (read==13) {
          utime = utime*10 + b-48;
        }
      }
      while (true) {
        byte b = babuffer[pos++];
        if (b==32) {
          read++;
          break;
        } else {
          stime = stime*10 + b-48;
        }
      }
      while (true) {
        byte b = babuffer[pos++];
        if (b==32) {
          read++;
          if (read==24) break;
        } else if (read==23) {
          mem = mem*10 + b-48;
        }
      }
      return ProcessStat.get(mem, pid, utime, stime, babuffer, npos, nlen);
    } catch (Exception e) {
      //e.printStackTrace();
      // Happens when the process disappear either before reading or during.
      return null;
    }
  }
}
