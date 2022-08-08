import m from 'mithril';
import { GaugeWithHistory } from '../../../../auto/pieroxy-conkw';
import { ColorUtils } from '../../../../utils/ColorUtils';
import { NumberUtils } from '../../../../utils/NumberUtils';
import { DashboardElement, DashboardElementAttrs } from '../DashboardElement';

export class GaugeWithHistoryComponent extends DashboardElement<GaugeWithHistoryAttrs> {
  private canvas:HTMLCanvasElement;
  private w:number;
  private h:number;
  private colors = [ColorUtils.getGaugeDefaultColor()];
  private fakeSeed=0;
  private iteration=-1;

  public view({attrs}:m.Vnode<GaugeWithHistoryAttrs>):m.Children {

    if (this.canvas && (attrs.currentData.iteration!=this.iteration)) {
      this.iteration = attrs.currentData.iteration
      let min = this.computeNumericValue(attrs.model.min, attrs.currentData.rawData);
      let max = this.computeNumericValue(attrs.model.max, attrs.currentData.rawData);
      let value = this.computeNumericValue(attrs.model.value, attrs.currentData.rawData);
      let log = attrs.model.logarithmic;

      if (attrs.currentData.useFakeDemoData) {
        value = Math.sin((this.fakeSeed++)/10)*50+50;
      }

      this.update(min, max, [value], log, attrs.currentData.metricGap);
    }

    let style= {
      height:((attrs.model.nbLinesHeight||1)-0.1)+"em"
    }

    return m("canvas", {
      style:style
    });
  }

  update(min: number, max: number, values: number[], log: boolean, metricGap: boolean) {
    var ctx = this.canvas.getContext('2d');
    if (!ctx) return;

    this.scroll();

    var bottom = 0;
    var bgColor = ColorUtils.getGaugeDefaultBackground();

    if (metricGap) {
        bgColor = "red";
    }

    ctx.fillStyle = bgColor;
    ctx.fillRect(this.w-1, 0, 1, this.h);

    for (var i = 0; i < this.colors.length; i++) {
        var value = values[i];
        ctx.fillStyle = this.colors[i];
        var posprc = NumberUtils.computePercentage(value, min, max, log);
        var hpx = posprc*this.h/100;

        ctx.fillRect(this.w-1, this.h-hpx-bottom, 1, hpx);
        bottom += hpx;
    }
/*
    if (max != this.maxValue && this.maxValue!=-1) {
        this.scroll();
        ctx.fillStyle = "orange";
        ctx.fillRect(this.w-1, 0, 1, this.h);
        this.maxValue = max;
    }
*/
  }

  oncreate({dom}:m.VnodeDOM<GaugeWithHistoryAttrs>) {
    this.canvas = <HTMLCanvasElement>dom;
    this.w = this.canvas.width = this.canvas.scrollWidth;
    this.h = this.canvas.height = this.canvas.scrollHeight;
    var ctx = this.canvas.getContext('2d');
    if (!ctx) return;
    ctx.fillStyle = ColorUtils.getGaugeDefaultBackground();
    ctx.fillRect(0,0,this.w,this.h)
  }

  scroll() {
    var ctx = this.canvas.getContext('2d');
    if (ctx) {
      ctx.save();
      ctx.translate(-1, 0);
      ctx.drawImage(this.canvas, 0, 0);
      ctx.restore();
    }
  }
}

export interface GaugeWithHistoryAttrs extends DashboardElementAttrs {
  model:GaugeWithHistory;
}