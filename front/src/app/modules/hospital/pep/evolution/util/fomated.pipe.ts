import { DatePipe } from '@angular/common';
import { Pipe, PipeTransform } from '@angular/core';
import { TranslocoService } from '@ngneat/transloco';

@Pipe({
	name: 'localizedDate',
	pure: false,
    standalone: true
})
export class LocalizedDatePipe implements PipeTransform {
	constructor(private translateService: TranslocoService) {}

	transform(value: any, pattern: string = 'longDate'): any {
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
		const datePipe: DatePipe = new DatePipe(locale);
		return datePipe.transform(value, pattern);
	}
}
