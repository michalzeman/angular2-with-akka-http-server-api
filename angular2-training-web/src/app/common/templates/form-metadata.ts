import {isNullOrUndefined} from "util";
import {Observable} from "rxjs";
export class DomainMetadata {

  key: string;
  label: string;
  required: boolean;
  controlType: string;
  table: boolean;

  constructor(options: {
    key: string,
    controlType: string,
    table: boolean,
    label?: string,
    required?: boolean
  }) {
    this.key = options.key;
    this.label = options.label || '';
    this.required = options.required || false;
    this.controlType = options.controlType || '';
    this.table = options.table;
  }
}

export class ValueObject {
  constructor(public list: any[],
              public value?: any) {
  }
}

export interface DropdownListItem {
  key: any,
  label: any,
  value: any
}

export class FormMetadata<V> {

  public defValue: V;

  list: DropdownListItem[];

  constructor(public metadata: DomainMetadata,
              value?: any | ValueObject) {
    // if (value && value[metadata.key]) {
    //   this.defValue = value[metadata.key];
    // }
    if (!isNullOrUndefined(value)) {
      if (value instanceof ValueObject) {
        if (!isNullOrUndefined(value.value)) {
          this.defValue = value.value[metadata.key];
        }
        this.list = value.list;
      } else {
        this.defValue = value[metadata.key];
      }
    }
  }

  static getInstance<T>(key: string, type: string, table: boolean, label: string, required: boolean, defVal: any): FormMetadata<T> {
    let test: ValueObject = {value: 1, list: []};
    return new FormMetadata<T>(
      new DomainMetadata({key: key, controlType: type, table: table, label: label, required: required}), defVal);
  }

  static getFormMetadataArray(metadataArray: DomainMetadata[], entity?: any): FormMetadata<any>[] {
    return metadataArray.map(item => {
      if (isNullOrUndefined(entity)) {
        return new FormMetadata(item);
      } else {
        return new FormMetadata(item, entity);
      }
    });
  }
}
