import { TitleCasePipe } from "@angular/common";
import { Pipe, PipeTransform } from "@angular/core";

@Pipe({name: 'equals', standalone: true})
export class EqualsPipe implements PipeTransform {
  transform(value: string, column: string) {
    if (value.includes(column.toLowerCase())) {
      let titleCasePipe = new TitleCasePipe()
      return titleCasePipe.transform(value.replace(column.toLowerCase(), "").replaceAll("_"," "));
    }
    
  }
}