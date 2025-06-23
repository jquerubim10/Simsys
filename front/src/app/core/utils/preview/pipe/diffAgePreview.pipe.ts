import { Pipe, PipeTransform } from "@angular/core";

@Pipe({name: 'diffAgePreview', standalone: true})
export class DifeAgePreviewPipe implements PipeTransform {
  transform(value: string, column?: string) {
    let data
    if (value.includes('nascimento') || !column) {  
        if (column) {
          if (value.includes(column.toLowerCase())) {
            data = value.replace(column.toLowerCase(), "").replaceAll("_","");
          } 
        } else {
          data = value;
        }
        
        const today = new Date();
        const birthDate = new Date(data);
        
        let yearsDiff = today.getFullYear() - birthDate.getFullYear();
        let  monthsDiff = today.getMonth() - birthDate.getMonth();
        let daysDiff = today.getDate() - birthDate.getDate();

        // Se a diferença em meses for negativa, ajuste os anos e meses
        if (monthsDiff < 0 || (monthsDiff === 0 && daysDiff < 0)) {
        yearsDiff--;
        }

        if (monthsDiff < 0) {
            monthsDiff += 12;
        }

        if (daysDiff < 0) {
            const prevMonthLastDay = new Date(today.getFullYear(), today.getMonth(), 0).getDate();
            daysDiff += prevMonthLastDay;
            monthsDiff--;
        }

        return `${yearsDiff} Ano(s), ${monthsDiff} Mês(es) e ${daysDiff} Dia(s)`;
    }
  }
}