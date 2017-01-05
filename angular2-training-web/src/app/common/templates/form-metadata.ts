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

export class FormMetadata<V> {

  public defValue: V;

  constructor(public metadata: DomainMetadata,
              value?: any
              // , public values?: V[]
              ) {
    if (value && value[metadata.key]) {
      this.defValue = value[metadata.key];
    }
  }

  static getInstance<T>(key: string, type: string, table: boolean, label: string, required: boolean, defVal: any): FormMetadata<T> {
    return new FormMetadata<T>(
      new DomainMetadata({key: key, controlType: type, table: table, label: label, required: required}), defVal);
  }

  static getFormMetadataArray(metadataArray:DomainMetadata[]): FormMetadata<any>[] {
    return metadataArray.map(item => new FormMetadata(item));
  }
}
