package ClassesUse;

import my_reflection.AutoInjectable;

public class ClassWithNoninitFields {
	// Поля без инициализации, помеченные аннотацией 'AutoInjectable'
	@AutoInjectable
	private Interface01 filed1;
	@AutoInjectable
	private Interface02 filed2;
	// Метод, использующий оба поля без инициализации
	public void foo() {
		filed1.doSomething01();
		filed2.doSomething02();
		System.out.println();
	}
	// Конструктор
	public ClassWithNoninitFields() {}
}
