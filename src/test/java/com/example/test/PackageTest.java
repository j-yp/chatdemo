package com.example.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.example.chat.config.utils.PackageUtil;

public class PackageTest {
	public static void main(String[] args) {
		List<String> packages = new ArrayList<>();
		packages.add("com.example.chat.config.anno");
		packages.add("org.mybatis.spring.annotation");
		List<Class<?>> classes = getClassesFromPackages(packages);
		System.out.println("size:"+classes.size());
		classes.forEach(clazz -> System.out.println(clazz.getName()));
	}
	
	public static List<Class<?>> getClassesFromPackages1(List<String> packages){
		List<Class<?>> list = packages.stream().reduce(new ArrayList<Class<?>>(), 
        		(clazzes, packageName) -> {try {
					clazzes.addAll(PackageUtil.getClassName(packageName).stream().map(className -> {
						try {
							return Class.forName(className);
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
						return null;
					}).collect(Collectors.toList()));
				} catch (IOException e) {
					e.printStackTrace();
				} return clazzes;}, 
        		(classes1, classes2) -> {classes1.addAll(classes2); return classes1;});
		return list;
	}
	
	public static List<Class<?>> getClassesFromPackages(List<String> packages){
		List<Class<?>> list = packages.stream().collect(
				Collectors.reducing(new ArrayList<Class<?>>(), 
				packageName -> {
					try {
						return PackageUtil.getClassName(packageName).stream().map(className -> {
							try {
								return Class.forName(className);
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}
							return null;
						}).collect(Collectors.toList());
					} catch (IOException e) {
						e.printStackTrace();
					}
					return null;
				}, 
				(classes1, classes2) -> {classes1.addAll(classes2); return classes1;}));
		return list;
	}
}
