package my_reflection;

import java.lang.reflect.*;

import ClassesUse.ClassWithNoninitFields;

public class Runner {
	public static void main( String args[] ) {
		// Получение объекта класса с инициализированными классом Injector полями
		// в соответствии с настройками в файле 'properties.txt'
        ClassWithNoninitFields cwnf = (ClassWithNoninitFields) ( new Injector()).inject(new ClassWithNoninitFields());
        // Запуск метода для инициализированного объекта класса
        cwnf.foo();
	}
}
