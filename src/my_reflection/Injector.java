package my_reflection;

import ClassesUse.ClassWithNoninitFields;
import java.io.*;
import java.nio.file.*;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.*;

// Класс для окончания инициализации объектов
public class Injector {
	public Object inject( Object sb ) {
		try {
			// Настройки из файла 'properties.txt' - каждому имени интерфейса (ключу)
			// соответствует имя класса (значение), его реализующего 
			Map<String,String> settings = new HashMap<String,String>();
			// Откываем файл ресурсов для чтения
			BufferedReader reader = Files.newBufferedReader( new File( "src/properties.txt").toPath() );
			// Строка, прочтенная из файла
			String line = "";
			// Пока файл не кончится
			while ( ( line = reader.readLine() ) != null ) {
				// Делим строку на части символом '='
				String parts[] = line.split("=");
				// Если образованных частей не 2
				if ( parts.length != 2 ) {
					System.out.println("Синтаксическая ошбика в файле 'properties.txt'");
					System.exit( 1 );
				}
				// Добавляем к настройкам еще одну пару интерфейс/класс
				settings.put(parts[0], parts[1]);
			}
			// Закрываем файл
			reader.close();
			// Извлекаем массив полей из объекта класса
			Field fields[] = sb.getClass().getDeclaredFields();
			// Перебираем поля в массиве
			for ( Field field : fields ) {
				try {
					// Разрешаем доступ к private полю
					field.setAccessible(true);
					// Если поле инициализировано
					if ( field.get(sb) != null ) {
						// Переходим к следующему полю
						continue;
					}
					// Если у поля нет аннотации 'AutoInjectable'
					if ( field.getAnnotation(AutoInjectable.class) == null ) {
						// Переходим к следующему полю
						continue;
					}
					// Класс данного поля (интерфейс)
					Class<?> fieldClass = field.getType();
					// Имя класса для инициализации поля field
					String fieldValueClassName = settings.get(fieldClass.getName());
					// Если в файле settings нет подходящей записи
					if ( fieldValueClassName == null ) {
						System.out.println("В файле settings.txt нет записи для интерфейса '" +
					                       fieldClass.getName());
						System.exit( 1 );
					}
					// Получаем класс для инициализации поля field
					Class<?> fieldValueClass = Class.forName(fieldValueClassName); 
					// ============
					// Массив интерфейсов, непосредственно (не через родителей) реализуемых
					// классом для инициализации
					Class<?>[] interFaces = fieldValueClass.getInterfaces();
					// Реализует ли класс для инициализации нужный интерфейс?
					boolean implementInterface = false;
					for ( int i = 0; i < interFaces.length; i++ ) {
						if ( interFaces[ i ].getCanonicalName().contentEquals(fieldClass.getCanonicalName()))
							implementInterface = true;
					}
					if ( ! implementInterface ) {
						System.out.println("Клас '" + fieldValueClassName + "' для инициализации поля '" + 
					                       field.getName() + "' не реализует нужный интерфейс");
						System.exit( 1 );
					}
					// ============
					// Инициализация поля field объектом, полученным через конструктор по умолчанию
					field.set( sb, fieldValueClass.getConstructor().newInstance() );
					
				} catch (IllegalArgumentException | IllegalAccessException e) {
					System.out.println("Ошибка некорректного аргумента или попытки создать объект, доступ к которому запрещен");
					e.printStackTrace();
					System.exit( 1 );
				} catch ( ClassNotFoundException e ) {
					System.out.println("В программе нет указанного в файле 'properties.txt' класса");
					System.exit( 1 );
				} catch (InstantiationException e) {
					System.out.println("Невозможно создать объект класса затребованного типа");
					e.printStackTrace();
					System.exit( 1 );
				} catch (InvocationTargetException e) {
					System.out.println("Конструктор объекта для инициализации поля выбросил исключение");
					e.printStackTrace();
					System.exit( 1 );
				} catch (NoSuchMethodException e) {
					System.out.println("В классе для инициализации поля нет конструктора без параметров");
					e.printStackTrace();
					System.exit( 1 );
				} catch (SecurityException e) {
					System.out.println("Менеджер безопасности запрещает доступ к полю");
					e.printStackTrace();
					System.exit( 1 );
				}
			}

		} catch (IOException e) {
			System.out.println("Ошибка ввода-вывода: " + e.getLocalizedMessage() );
			System.exit( 1 );
		}
		
		return sb;
	}
}
