package com.freeloop.student;
import java.util.Scanner;
public class StudentManagerApp {
    public static void main(String[] args) {
        Student[] stuArr=new Student[10];
        int size=0;
        Scanner In=new Scanner(System.in);
        while(true){
            System.out.println("""
                    输入需要的操作：
                    0：退出操作
                    1：添加学生信息
                    2：查看学生列表
                    3：根据学号查询学生
                    4：修改学生信息
                    5：删除学生信息
                    """
            );

            String chooseText=In.nextLine();
            int choose;
            try {
                choose =Integer.parseInt(chooseText);
            }catch(NumberFormatException e){
                System.out.println("输入操作必须是数字");
                continue;
            }

            switch (choose){
                case 0:
                    return;
                case 1:
                    if(size>= stuArr.length){
                        System.out.println("学生信息已满");
                        break;
                    }
                    System.out.println("输入学生学号");
                    String id=In.nextLine();
                    boolean tag=false;
                    for(int i=0;i<size;++i){
                        if(stuArr[i].getId().equals(id)){
                            tag=true;
                            break;
                        }
                    }
                    if(tag){
                        System.out.println("学号重复");
                        break;
                    }
                    System.out.println("输入学生姓名");
                    String name=In.nextLine();
                    System.out.println("输入学生年龄");
                    String ageText=In.nextLine();
                    int age;
                    try {
                        age = Integer.parseInt(ageText);
                    }catch(NumberFormatException e){
                        System.out.println("年龄必须是数字");
                        continue;
                    }
                    System.out.println("输入学生性别");
                    String gender=In.nextLine();
                    System.out.println("输入学生手机号");
                    String phone =In.nextLine();

                    Student stu;
                    try{
                        stu=new Student(id,name,age,gender,phone);
                    }catch(IllegalArgumentException e){
                        System.out.println(e.getMessage());
                        break;
                    }
                    stuArr[size]=stu;
                    size++;
                    System.out.println("学生添加成功");
                    break;

                case 2:

                    if(size==0){
                        System.out.println("暂无学生信息");
                        break;
                    }
                    for(int i=0;i<size;++i){
                        System.out.println(stuArr[i].getId());
                        System.out.println(stuArr[i].getName());
                        System.out.println(stuArr[i].getAge());
                        System.out.println(stuArr[i].getGender());
                        System.out.println(stuArr[i].getPhone());
                    }
                    break;
                case 3:
                    boolean id_tag=false;
                    System.out.println("输入查询的学生学号");
                    String Id=In.nextLine();
                    for(int i=0;i<size;++i){
                        if(stuArr[i].getId().equals(Id)){
                            id_tag=true;
                            System.out.println("学生信息如下：");
                            System.out.println(stuArr[i].getId());
                            System.out.println(stuArr[i].getName());
                            System.out.println(stuArr[i].getAge());
                            System.out.println(stuArr[i].getGender());
                            System.out.println(stuArr[i].getPhone());
                            break;
                        }
                    }
                    if(!id_tag){
                        System.out.println("未查找到该学生信息");
                    }
                    break;
                case 4:
                    System.out.println("输入需要修改学生信息的学号：");
                    String change_id=In.nextLine();
                    boolean change_tag=false;
                    for(int i=0;i<size;++i){
                        if(change_id.equals(stuArr[i].getId())){
                            change_tag =true;
                            System.out.println("输入修改的年龄：");
                            String change_ageText=In.nextLine();
                            int change_age;
                            try{
                                change_age=Integer.parseInt(change_ageText);
                            }catch(NumberFormatException e){
                                System.out.println("年龄必须是数字");
                                break;
                            }
                            System.out.println("输入修改的姓名：");
                            String change_name=In.nextLine();
                            System.out.println("输入修改的性别：");
                            String change_gender=In.nextLine();
                            System.out.println("输入修改的手机号：");
                            String change_phone=In.nextLine();
                            try{
                                Student change_student=new Student(stuArr[i].getId(),change_name,change_age,change_gender,change_phone);
                                stuArr[i]=change_student;
                                System.out.println("修改成功");
                            }catch(IllegalArgumentException e){
                                System.out.println("修改失败"+e.getMessage());
                                break;
                            }
                            break;
                        }
                    }
                    if(!change_tag){
                        System.out.println("学生信息不存在");
                        break;
                    }
                    break;

                case 5:
                    System.out.println("输入需要删除的学生学号");
                    String del_id=In.nextLine();
                    boolean del_tag=false;
                    for(int i=0;i<size;++i){
                        if(stuArr[i].getId().equals(del_id)){
                            del_tag=true;
                            for(int j=i;j<size-1;++j){
                                stuArr[j]=stuArr[j+1];
                            }
                            System.out.println("删除成功");
                            size--;
                            stuArr[size]=null;
                            break;
                        }
                    }
                    if(!del_tag){
                        System.out.println("没有该学生信息");
                        break;
                    }

                    break;

                default:
                    System.out.println("输入有错误");
            }

        }
    }
}
