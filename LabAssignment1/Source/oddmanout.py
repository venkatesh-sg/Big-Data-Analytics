array = [3,4,5,6,8,5,6,3,8];
if __name__ == '__main__':
    for num in array:
        a=0;
        for number in array:
            if(num==number):
                a=a+1;
        if(a!=2):
            print(num);
            exit
