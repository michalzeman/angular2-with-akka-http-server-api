import {Injectable}   from '@angular/core';
// import {FormBuilder, Validators, ControlGroup, Control} from '@angular/common';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {FormMetadata} from "./form-metadata";

@Injectable()
export class FormControlService {

  constructor() {
  }

  /**
   * Return created Form control group
   * @param metadata
   * @returns {modelModule.ControlGroup}
   */
  getControlGroup(metadata: FormMetadata<any>[]): FormGroup {
    let group = {};

    metadata.forEach(item => {
      // group[item.metadata.key] = item.metadata.required ? [item.defValue || '', Validators.required] : [item.defValue || ''];
      group[item.metadata.key] = item.metadata.required ? new FormControl(item.defValue || '', Validators.required)
        : new FormControl(item.defValue || '');
    });
    return new FormGroup(group);
  }
}
