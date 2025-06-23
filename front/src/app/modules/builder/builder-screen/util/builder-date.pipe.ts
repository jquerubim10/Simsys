import { DatePipe, formatDate } from '@angular/common';
import { Pipe, PipeTransform } from '@angular/core';
import { TranslocoService } from '@ngneat/transloco';

@Pipe({
	name: 'builderDatePipe',
	pure: false,
    standalone: true
})
export class BuilderDatePipe implements PipeTransform {
	constructor(private translateService: TranslocoService) {}

	transform(value: any, format): any {
        let locale = '';
        switch(this.translateService.getActiveLang()) {
            case 'br' : {
                locale = 'pt-br'
                break;
            }
            case 'en' : {
                locale = 'en-us'
                break;
            }
        }
		return formatDate(value, format ? format : "dd/mm/yyyy hh:mm", locale)
	}
}
