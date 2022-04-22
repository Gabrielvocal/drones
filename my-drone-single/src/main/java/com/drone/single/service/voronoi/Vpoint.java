package com.drone.single.service.voronoi;

public class Vpoint {

    private double[] coordinates;


    public Vpoint (double... crd) {

        coordinates = new double[crd.length];
        System.arraycopy(crd, 0, coordinates, 0, crd.length);
    }


    public boolean equals (Object other) {
        if (!(other instanceof Vpoint)) return false;
        Vpoint p = (Vpoint) other;
        if (this.coordinates.length != p.coordinates.length) return false;
        for (int i = 0; i < this.coordinates.length; i++)
            if (this.coordinates[i] != p.coordinates[i]) return false;
        return true;
    }


    public double getXY (int i) {//坐标值
        return this.coordinates[i];
    }


    public Vpoint addToPoint (double... crd) {
        double[] result = new double[coordinates.length + crd.length];
        System.arraycopy(coordinates, 0, result, 0, coordinates.length);
        System.arraycopy(crd, 0, result, coordinates.length, crd.length);
        return new Vpoint(result);
    }

    //点的数量积
    public double dotProduct (Vpoint p) {
        int len = 2;
        double sum = 0;
        for (int i = 0; i < len; i++)
            sum += this.coordinates[i] * p.coordinates[i];
        return sum;
    }

    //点向量长度
    public double length () {
        return Math.sqrt(this.dotProduct(this));
    }

    public Vpoint sub (Vpoint p) {
        int len = 2;
        double[] crd = new double[len];
        for (int i = 0; i < len; i++)
            crd[i] = this.coordinates[i] - p.coordinates[i];
        return new Vpoint(crd);
    }


    public Vpoint add (Vpoint p) {

        double[] crd = new double[2];
        for (int i = 0; i < 2; i++)
            crd[i] = this.coordinates[i] + p.coordinates[i];
        return new Vpoint(crd);
    }


    public double angle (Vpoint p) {
        return Math.acos(this.dotProduct(p) / (this.length() * p.length()));
    }


    public Vpoint bisector (Vpoint point) {//两点中垂线点
        Vpoint diff = this.sub(point);
        Vpoint sum = this.add(point);
        double dp = diff.dotProduct(sum);
        return diff.addToPoint(-dp / 2);
    }


    public static double det (Vpoint[] matrix) {//矩阵行列式值
        boolean[] col = new boolean[matrix.length];
        for (int i = 0; i < matrix.length; i++) col[i] = true;
        try {return det(matrix, 0, col);}
        catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Matrix is wrong ");
        }
    }


    private static double det(Vpoint[] matrix, int row, boolean[] col) {
        if (row == matrix.length) return 1;
        double sum = 0;
        int flag = 1;
        for (int n = 0; n < col.length; n++) {
            if (!col[n]) continue;
            col[n] = false;
            sum += flag * matrix[row].coordinates[n] *
                    det(matrix, row+1, col);
            col[n] = true;
            flag = -flag;
        }
        return sum;
    }


    public static Vpoint crossProduct (Vpoint[] matrix) {//向量积
        int len = matrix.length + 1;
        boolean[] col = new boolean[len];
        for (int i = 0; i < len; i++) col[i] = true;
        double[] value = new double[len];
        int flag = 1;
        try {
            for (int i = 0; i < len; i++) {
                col[i] = false;
                value[i] = flag * det(matrix, 0, col);
                col[i] = true;
                flag = -flag;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Matrix is wrong shape");
        }
        return new Vpoint(value);
    }


    public static double valOfTri (Vpoint[] triangle) {
        Vpoint[] matrix = new Vpoint[triangle.length];
        for (int i = 0; i < matrix.length; i++)
            matrix[i] = triangle[i].addToPoint(1);
        int dem= 1;
        for (int i = 1; i < matrix.length; i++) dem = dem*i;
        return det(matrix) / dem;
    }


    public int[] pointToTri (Vpoint[] triangle) {//判断点与三角的关系

        int dimension = triangle.length - 1;
        if (this.coordinates.length != dimension)
            throw new IllegalArgumentException("Dimension mismatch");
        Vpoint[] matrix = new Vpoint[dimension+1];
        double[] coords = new double[dimension+2];
        for (int j = 0; j < coords.length; j++) coords[j] = 1;
        matrix[0] = new Vpoint(coords);
        for (int i = 0; i < dimension; i++) {//将输入点的与三角形的横坐标与纵坐标分别用coords数组装好，放入matrix中
            coords[0] = this.coordinates[i];
            for (int j = 0; j < triangle.length; j++)
                coords[j+1] = triangle[j].coordinates[i];
            matrix[i+1] = new Vpoint(coords);
        }
        Vpoint vector = crossProduct(matrix);//求向量积
        double content = vector.coordinates[0];
        int[] result = new int[dimension+1];
        for (int i = 0; i < result.length; i++) {
            double value = vector.coordinates[i+1];
            if (Math.abs(value) <= 1.0e-6 * Math.abs(content)) result[i] = 0;
            else if (value < 0) result[i] = -1;
            else result[i] = 1;
        }
        if (content < 0) {
            for (int i = 0; i < result.length; i++) result[i] = -result[i];
        }
        if (content == 0) {
            for (int i = 0; i < result.length; i++) result[i] = Math.abs(result[i]);
        }
        return result;
    }
    //判断点是否在三角区域外
    public Vpoint beOutTri (Vpoint[] triangle) {
        int[] result = this.pointToTri(triangle);
        for (int i = 0; i < result.length; i++) {
            if (result[i] > 0) return triangle[i];
        }
        return null;
    }
    //判断点是否在三角区域边上
    public Vpoint beOnTri (Vpoint[] triangle) {
        int[] result = this.pointToTri(triangle);
        Vpoint witness = null;
        for (int i = 0; i < result.length; i++) {
            if (result[i] == 0) witness = triangle[i];
            else if (result[i] > 0) return null;
        }
        return witness;
    }
    //判断点是否在三角区域内
    public boolean beInTri (Vpoint[] triangle) {
        int[] result = this.pointToTri(triangle);
        for (int r: result) if (r >= 0) return false;
        return true;
    }
    //点和外接圆位置。里面-1，上0，外面+1
    public int PointCircleOfTri (Vpoint[] triangle) {
        Vpoint[] matrix = new Vpoint[triangle.length + 1];
        for (int i = 0; i < triangle.length; i++)
            matrix[i] = triangle[i].addToPoint(1, triangle[i].dotProduct(triangle[i]));
        matrix[triangle.length] = this.addToPoint(1, this.dotProduct(this));
        double d = det(matrix);
        int val = (d < 0)? -1 : ((d > 0)? +1 : 0);
        if (valOfTri(triangle) < 0) val = - val;
        return val;
    }

    //外接圆圆心
    public static Vpoint circlecenterOfTri (Vpoint[] triangle) {

        Vpoint[] matrix = new Vpoint[2];
        for (int i = 0; i < 2; i++)
            matrix[i] = triangle[i].bisector(triangle[i+1]);
        Vpoint centerPoint = crossProduct(matrix);
        double yval = centerPoint.coordinates[2];
        double[] val = new double[2];
        for (int i = 0; i < 2; i++) val[i] = centerPoint.coordinates[i] / yval;
        return new Vpoint(val);
    }

}
