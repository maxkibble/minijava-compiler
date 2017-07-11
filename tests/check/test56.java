class test56{
    public static void main(String[] a){
	System.out.println(new A().start());
    }
}

class B extends A{
    public int getC(){
        return c + 1;
    }
}

class A{
    B b;
    int c;

    public int setC(int d){
        c = d;
        return 0;
    }

    public int getC(){
        return c;
    }

    public int start(){
    A a;
    int r;
	b = new B();
    r = b.setC(2);
	a = b;
    r = a.getC();
	return r;
    }
}
