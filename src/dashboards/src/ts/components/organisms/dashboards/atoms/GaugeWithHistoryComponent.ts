import m from 'mithril';
import { SimpleGauge } from '../../../../auto/pieroxy-conkw';
import { NumberUtils } from '../../../../utils/NumberUtils';
import { DashboardElement, DashboardElementAttrs } from '../DashboardElement';

export class GaugeWithHistoryComponent extends DashboardElement<GaugeWithHistoryAttrs> {
  private canvas:HTMLCanvasElement;
  private w:number;
  private h:number;
  private colors = ["green"];
  private fakeSeed=0;

  public view({attrs}:m.Vnode<GaugeWithHistoryAttrs>):m.Children {

    if (this.canvas) {
      let min = this.computeNumericValue(attrs.model.min, attrs.currentData.rawData);
      let max = this.computeNumericValue(attrs.model.max, attrs.currentData.rawData);
      let value = this.computeNumericValue(attrs.model.value, attrs.currentData.rawData);
      let log = attrs.model.logarithmic;

      if (attrs.currentData.useFakeDemoData) {
        value = Math.sin((this.fakeSeed++)/10)*50+50;
      }

      this.update(min, max, [value], log, attrs.currentData.metricGap);
    }
    
    return m("canvas");
  }

  update(min: number, max: number, values: number[], log: boolean, metricGap: boolean) {
    var ctx = this.canvas.getContext('2d');
    if (!ctx) return;

    this.scroll();

    var bottom = 0;
    var bgColor = "#000";

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
    console.log(`${this.w} ${this.h} ${metricGap} ${JSON.stringify(values)} ${min} ${max} ${bottom}`)
/*
    if (max != this.maxValue && this.maxValue!=-1) {
        this.scroll();
        ctx.fillStyle = "orange";
        ctx.fillRect(this.w-1, 0, 1, this.h);
        this.maxValue = max;
    }
*/
  }

  oncreate(vnode:m.VnodeDOM<GaugeWithHistoryAttrs>) {
    this.canvas = <HTMLCanvasElement>vnode.dom;
    this.w = this.canvas.width = this.canvas.scrollWidth;
    this.h = this.canvas.height = this.canvas.scrollHeight;
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
  model:SimpleGauge;
}