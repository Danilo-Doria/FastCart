import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductoDialog } from './producto-dialog';

describe('ProductoDialog', () => {
  let component: ProductoDialog;
  let fixture: ComponentFixture<ProductoDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductoDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(ProductoDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
