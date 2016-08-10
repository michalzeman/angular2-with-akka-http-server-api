import { Injectable }   from '@angular/core';
import {FormBuilder, Validators, ControlGroup, Control} from '@angular/common';
import {FormMetadata} from "./form-metadata";

@Injectable()
export class FormControlService {

  constructor(private formBuilder: FormBuilder){}

  /**
   * Return created Form control group
   * @param metadata
   * @returns {modelModule.ControlGroup}
     */
  getControlGroup(metadata: FormMetadata<any>[]):ControlGroup {
    let group = {};

    metadata.forEach(item => {
      group[item.metadata.key] = item.metadata.required ? [item.defValue || '', Validators.required] : [item.defValue || ''];
    });
    return this.formBuilder.group(group);
  }
}
