import { DatePipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SdkAutocompleteItem, SdkFormBuilderConfiguration, SdkFormBuilderField, SdkFormFieldGroupConfiguration, SdkMultiselectItem } from '@maggioli/sdk-controls';
import { each, get, isEmpty, isObject, isUndefined, set } from 'lodash-es';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { TabellatiComboProvider } from '../providers/tabellati-combo.provider';
import { AppCostants } from '../utils/dgue-constants';
import { StoreService } from './storeService';

@Injectable({
  providedIn: 'root'
})
export class FormBuilderService {
  assetsBaseURL: string = 'assets/cms/pages/';

  constructor(private httpClient: HttpClient,public store : StoreService,public datepipe: DatePipe, public tabellatiComboProvider: TabellatiComboProvider ) {}

  getJson(fileName): Observable<any>{
    return this.httpClient.get(this.assetsBaseURL + fileName + '.json');
  }

  getFormRequest(formBuilderConfig: SdkFormBuilderConfiguration){
    let request: any = {};
    
    each(formBuilderConfig.fields, (field: any) => {
        if (field.type === 'FORM-SECTION') {
           request = this.elaborateSection(field, request);           
        } else if (field.type === 'FORM-GROUP') {
            request = this.elaborateGroup(field, request);
        } else {
            request = this.elaborateOne(field, request);
        }
    });
    return request;
  }

  private elaborateOne(field: SdkFormBuilderField, request: any): any {
    if (isObject(field) && !isEmpty(field.mappingOutput)) {
        if (field.type === 'COMBOBOX') {
            if (!isUndefined(field.data)) {                
                if(field.data.key==='false'){
                    field.data.key=false as boolean;;
                }
                if(field.data.key==='true'){
                    field.data.key=true as boolean;
                }
                set(request, field.mappingOutput, field.data.key);
            }           
        } else if (field.type === 'AUTOCOMPLETE') {
            if (field.data != null) {
                let item: SdkAutocompleteItem = get(field, 'data');
                set(request, field.mappingOutput, item._key);
            }
        } else if(field.type === 'MULTISELECT'){
            field.itemsProvider().subscribe((result: Array<SdkMultiselectItem>) => {                
                let newData = [];
                if (!isEmpty(result) && !isEmpty(field.data)) {                        
                    for(let i = 0; i < field.data.length; i++){ 
                        field.data[i].value = field.data[i].key;
                    }
                    for(let i = 0; i < field.data.length; i++){    
                        let isPresent = false;                    
                        for(let j = 0; j < result.length; j++){                            
                            if(result[j].key == field.data[i].key){                              
                                isPresent = true;
                            }
                        }
                        if(isPresent){
                            newData.push(field.data[i]);
                        }
                    }                    
                }
                set(request, field.mappingOutput, newData);

                if(newData.length == 0 && (field.code === 'enrolmentProfessionalRegisterLotID' || field.code === 'enrolmentTradeRegisterLotID')){
                    set(request, field.mappingOutput, [{key:"",value:""}]);
                }
            })
                                                               
        } else if(field.type === 'DATEPICKER'){
            if (!isUndefined(field.data)) {
                let date = this.datepipe.transform(field.data?.getTime(), 'yyyy-MM-dd');               
                set(request, field.mappingOutput, date.toString());
            }
        } else if(field.type === 'TEXT' && !isUndefined(field.data) && !isUndefined(field.itemsProviderCode)){
                let comboData = this.tabellatiComboProvider.getTabellatoKey(field.listCode,field.data);
                set(request, field.mappingOutput, comboData);
        
            
        } else{
            if (!isUndefined(field.data)) {
                set(request, field.mappingOutput, field.data);
            } else{
                if(field.code === 'enrolmentProfessionalRegisterLotID' ||
                    field.code === 'enrolmentProfessionalRegisterRegisterName' ||
                    field.code === 'enrolmentProfessionalRegisterUrl' ||                    
                    field.code === 'enrolmentTradeRegisterLotID' ||
                    field.code === 'enrolmentTradeRegisterRegisterName' ||
                    field.code === 'enrolmentTradeRegisterUrl' ||
                    
                    field.code === 'serviceContractsAuthorisationRegisterName' ||
                    field.code === 'serviceContractsAuthorisationUrl' ||
                    
                    field.code === 'serviceContractsMembershipRegisterName' ||
                    field.code === 'serviceContractsMembershipUrl' ){
                        set(request, field.mappingOutput, null);
                    }                
            }
        }
    }
    return request;
}

private elaborateSection(field: any, request: any) {
    
    each(field.fieldSections, (one: any) => {
        if (one.type === 'FORM-SECTION') {

            request = this.elaborateSection(one, request);
        } else if (one.type === 'FORM-GROUP') {
            request = this.elaborateGroup(one, request);
        } else {
            request = this.elaborateOne(one, request);
        }
    });
    return request;
}

private elaborateGroup(field: any, request: any): any {
    if (field.visible !== false) {
        let newRestObjects: Array<any>;
        if (!isEmpty(field.mappingOutput)) {
            newRestObjects = get(request, field.mappingOutput);
            if (isUndefined(newRestObjects)) {
                newRestObjects = new Array();
            }
        }

        if (isObject(newRestObjects)) {
            each(field.fieldGroups, (group: SdkFormFieldGroupConfiguration) => {
                let singleIterationObject: any = {};
                each(group.fields, (one: any) => {
                    if (one.type === 'FORM-SECTION') {
                        singleIterationObject = this.elaborateSection(one, singleIterationObject);
                    } else if (one.type === 'FORM-GROUP') {
                        singleIterationObject = this.elaborateGroup(one, singleIterationObject);
                    } else {
                        singleIterationObject = this.elaborateOne(one, singleIterationObject);
                    }
                });
                newRestObjects.push(singleIterationObject);
            });
            set(request, field.mappingOutput, newRestObjects);
        } else {
            each(field.fieldGroups, (group: SdkFormFieldGroupConfiguration, index: number) => {
                each(group.fields, (one: any) => {
                    if (one.type === 'FORM-SECTION') {
                        request = this.elaborateSection(one, request);
                    } else if (one.type === 'FORM-GROUP') {
                        request = this.elaborateGroup(one, request);
                    } else {
                        request = this.elaborateOne(one, request);
                    }
                });
                field.fieldGroups[index] = group;
            });
        }
    }
    return request;
}


  

}