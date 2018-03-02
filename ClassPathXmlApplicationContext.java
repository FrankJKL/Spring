package cn.kunlun.spring;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class ClassPathXmlApplicationContext implements BeanFactory {

	private Map<String, Object> beans = new HashMap<String, Object>();

	// IOC:Inverse of Control DI:Dependency Injection
	public ClassPathXmlApplicationContext() throws Exception {
		SAXBuilder sb = new SAXBuilder();
		Document document = sb.build(this.getClass().getClassLoader().getResourceAsStream("beans.xml"));
		Element root = document.getRootElement();
		List<Element> list = root.getChildren("bean");
		for (Element element : list) {
			String id = element.getAttributeValue("id");
			String clazz = element.getAttributeValue("class");
			Object o = Class.forName(clazz).newInstance();
			System.out.println(id);
			System.out.println(o);
			beans.put(id, o);

			// 实现依赖注入
			for (Element propertyElement : (List<Element>) element.getChildren("property")) {
				String name = propertyElement.getAttributeValue("name");
				String bean = propertyElement.getAttributeValue("bean");
				Object beanObject = beans.get(bean);
				// 通过set方法进行依赖注入
				String methodName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
				System.out.println("method name = " + methodName);
				// 把beanObject对象注入到o对象的name属性
				Method m = o.getClass().getMethod(methodName, beanObject.getClass().getInterfaces()[0]);
				m.invoke(o, beanObject);
			}
		}
	}

	@Override
	public Object getBean(String id) {
		return beans.get(id);
	}

}
