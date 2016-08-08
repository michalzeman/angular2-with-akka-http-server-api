import {Component, Input, OnInit} from '@angular/core';
import {ControlGroup}     from '@angular/common';
import {FormMetadata} from "../templates/domain-metadata";

@Component({
  selector: 'form-item',
  templateUrl: './form-item.html',
  directives: []
})
export class FormItemComponent implements OnInit {

  @Input() data: FormMetadata<any>;

  @Input() form: ControlGroup;

  constructor() {

  }

  ngOnInit() {
    console.log(this.data, this.form);
  }

  get isValid() {
    return this.form.controls[this.data.metadata.key].valid;
  }

}
