package org.soky.bot.pk2.entityextractors;

import java.util.HashSet;
import java.util.Set;

public class Drive {

	
	public static void main(String args[]) { 
		
		Set<Foo> set = new HashSet<>() ; 
		
		Foo foo1 = new Foo() ; 
		foo1.name = "Amr" ; 
		foo1.age = 25 ; 
		
		Foo foo2 = new Foo() ; 
		foo2.name = "Amr" ; 
		foo2.age = 25 ; 
		
		set.add(foo1);
		set.add(foo2) ; 
		
		System.out.println("Set Size : " + set.size()) ; 
		
	}
	
	
	public static class Foo { 
		
		public String name ; 
		public int age ;
		
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + age;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}
		
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Foo other = (Foo) obj;
			if (age != other.age)
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		} 
		
		
		
		
		
	}
}
