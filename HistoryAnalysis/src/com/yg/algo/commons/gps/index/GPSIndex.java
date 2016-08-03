package com.yg.algo.commons.gps.index;

import com.yg.algo.commons.gps.bean.GPSElement;
import com.yg.algo.commons.gps.bean.GPSPoint;
import com.yg.algo.commons.gps.bean.GPSSegment;

import java.util.*;

/**
 * Created by YCZhang on 6/25/15.
 */
public class GPSIndex<E extends Comparable<E>> implements Iterable<Map.Entry<GPSIndex<E>.Grid, Set<E>>> {

	private static final int[][] OFFSET_EDGE = new int[][] {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
	private static final int[][] OFFSET_CORNER = new int[][] {{-1, -1}, {-1, 1}, {1, 1}, {1, -1}};

	public class Grid implements Comparable<Grid> {
		public final int x;
		public final int y;

		private Grid(double longitude, double latitude) {
			x = (int) (longitude / ACCURACY);
			y = (int) (latitude / ACCURACY);
		}

		private Grid(int x, int y) {
			this.x = x;
			this.y = y;
		}

		/**
		 * 根据偏移量计算对应的Grid
		 * @param offset_x
		 * @param offset_y
		 * @return
		 */
		public Grid offset(int offset_x, int offset_y) {
			return new Grid(this.x + offset_x, this.y + offset_y);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof GPSIndex.Grid)) return false;

			Grid grid = (Grid) o;

			if (x != grid.x) return false;
			if (y != grid.y) return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = x;
			result = 31 * result + y;
			return result;
		}

		@Override
		public int compareTo(Grid o) {
			return this.x != o.x ? this.x - o.x : this.y - o.y;
		}

		@Override
		public String toString() {
			return "Grid{" +
					"x=" + x +
					", y=" + y +
					'}';
		}

		/**
		 * 获取左下角坐标
		 * @return
		 */
		public GPSPoint toLBCorner() {
			return new GPSPoint(x * ACCURACY, y * ACCURACY);
		}

		/**
		 * 获取左上角坐标
		 * @return
		 */
		public GPSPoint toLTCorner() {
			return new GPSPoint(x * ACCURACY, y * ACCURACY + ACCURACY);
		}

		/**
		 * 获取右上角坐标
		 * @return
		 */
		public GPSPoint toRTCorner() {
			return new GPSPoint(x * ACCURACY + ACCURACY, y * ACCURACY + ACCURACY);
		}

		/**
		 * 获取右下角坐标
		 * @return
		 */
		public GPSPoint toRBCorner() {
			return new GPSPoint(x * ACCURACY + ACCURACY, y * ACCURACY);
		}
	}

	private final double ACCURACY;

	private Map<Grid, Set<E>> map;


	public GPSIndex(double accuracy) {
		this.ACCURACY = accuracy;
		this.map = new TreeMap<>();
	}

	/**
	 * 获取Grid数量
	 * @return
	 */
	public int size() {
		return map.size();
	}

	/**
	 * 遍历
	 * @return
	 */
	@Override
	public Iterator<Map.Entry<GPSIndex<E>.Grid, Set<E>>> iterator() {
		return map.entrySet().iterator();
	}

	/**
	 * 获取GPSElement覆盖的Grid集合
	 * @param gps
	 * @return 覆盖的Grid集合
	 */
	public Set<Grid> parse2Grids(GPSElement gps) {
		if (gps instanceof GPSPoint)
			return parse2Grids((GPSPoint) gps);
		else if (gps instanceof GPSSegment)
			return parse2Grids((GPSSegment) gps);

		return null;
	}

	/**
	 * 获取GPSPoint覆盖的Grid集合
	 * @param point
	 * @return 覆盖的Grid集合
	 */
	public Set<Grid> parse2Grids(GPSPoint point) {
		Set<Grid> ret = new TreeSet<>();
		ret.add(new Grid(point.lng_d, point.lat_d));
		return ret;
	}

	/**
	 * 获取GPSSegment覆盖的Grid集合
	 * @param segment
	 * @return 覆盖的Grid集合
	 */
	public Set<Grid> parse2Grids(GPSSegment segment) {
		Set<Grid> ret = new TreeSet<>();

		Grid head = new Grid(segment.end1.lng_d, segment.end1.lat_d);
		Grid tail = new Grid(segment.end2.lng_d, segment.end2.lat_d);
		ret.add(head);
		ret.add(tail);

		Grid current = head;
		while (true) {
			boolean flag = false;
			// 先遍历4边相邻的Grid
			for (int i = 0; i < 4; ++i) {
				Grid next = current.offset(OFFSET_EDGE[i][0], OFFSET_EDGE[i][1]);
				if (!ret.contains(next) && isCrossed(next, segment)) {
					ret.add(next);
					current = next;
					flag = true;
					break;
				}
			}
			if (flag)
				continue;
			// 再遍历4角相邻的Grid
			for (int i = 0; i < 4; ++i) {
				Grid next = current.offset(OFFSET_CORNER[i][0], OFFSET_CORNER[i][1]);
				if (!ret.contains(next) && isCrossed(next, segment)) {
					ret.add(next);
					current = next;
					flag = true;
					break;
				}
			}
			if (!flag)
				break;
		}

		return ret;
	}

	/**
	 * 判断Grid和GPSSegment是否相交，上边缘和右边缘重合不算
	 * @param grid
	 * @param segment
	 * @return true, 相交；false, 不相交
	 */
	private boolean isCrossed(Grid grid, GPSSegment segment) {
		GPSPoint p0 = grid.toLBCorner();
		GPSPoint p1 = grid.toLTCorner();
		GPSPoint p2 = grid.toRTCorner();
		GPSPoint p3 = grid.toRBCorner();

		boolean isOnSegment0 = isOnSegment(p0, segment);
		boolean isOnSegment1 = isOnSegment(p1, segment);
		boolean isOnSegment2 = isOnSegment(p2, segment);
		boolean isOnSegment3 = isOnSegment(p3, segment);

		// 左边缘在线上
		if (isOnSegment0 && isOnSegment1)
			return true;
		// 下边缘在线上
		if (isOnSegment0 && isOnSegment3)
			return true;
		// 副对角线在线上
		if (isOnSegment0 && isOnSegment2)
			return true;
		// 主对角线在线上
		if (isOnSegment1 && isOnSegment3)
			return true;

		// 左边缘相交
		if (isCrossed(segment.end1, segment.end2, p0, p1))
			return true;
		// 上边缘相交
		if (isCrossed(segment.end1, segment.end2, p1, p2))
			return true;
		// 右边缘相交
		if (isCrossed(segment.end1, segment.end2, p2, p3))
			return true;
		// 下边缘相交
		if (isCrossed(segment.end1, segment.end2, p3, p0))
			return true;

		return false;
	}

	/**
	 * 判断线段是否相交，相交点不能是线段端点
	 * @param p00 线段0的端点0
	 * @param p01 线段0的端点1
	 * @param p10 线段1的端点0
	 * @param p11 线段1的端点1
	 * @return true, 相交; false, 不相交
	 */
	private boolean isCrossed(GPSPoint p00, GPSPoint p01, GPSPoint p10, GPSPoint p11) {
		GPSPoint v0001 = vector(p00, p01);
		GPSPoint v0010 = vector(p00, p10);
		GPSPoint v0011 = vector(p00, p11);

		if (crossProduct(v0001, v0010) * crossProduct(v0001, v0011) >= 0)
			return false;

		GPSPoint v1011 = vector(p10, p11);
		GPSPoint v1000 = vector(p10, p00);
		GPSPoint v1001 = vector(p10, p01);

		if (crossProduct(v1011, v1000) * crossProduct(v1011, v1001) >= 0)
			return false;

		return true;
	}

	/**
	 * 判断GPSPoint是否在GPSSegment上
	 * @param p0
	 * @param segment
	 * @return true, 在线上; false, 不在线上
	 */
	private boolean isOnSegment(GPSPoint p0, GPSSegment segment) {
		GPSPoint p10 = segment.end1;
		GPSPoint p11 = segment.end2;

		if (p0.lng_d < p10.lng_d && p0.lng_d < p11.lng_d)
			return false;
		if (p0.lng_d > p10.lng_d && p0.lng_d > p11.lng_d)
			return false;
		if (p0.lat_d < p10.lat_d && p0.lat_d < p11.lat_d)
			return false;
		if (p0.lat_d > p10.lat_d && p0.lat_d > p11.lat_d)
			return false;

		GPSPoint v010 = vector(p0, p10);
		GPSPoint v011 = vector(p0, p11);

		if (crossProduct(v010, v011) != 0)
			return false;

		return true;
	}

	/**
	 * 算叉积
	 * @param p0
	 * @param p1
	 * @return
	 */
	private double crossProduct(GPSPoint p0, GPSPoint p1) {
		return p0.lng_d * p1.lat_d - p0.lat_d * p1.lng_d;
	}

	/**
	 * 计算向量
	 * @param p0
	 * @param p1
	 * @return
	 */
	private GPSPoint vector(GPSPoint p0, GPSPoint p1) {
		return new GPSPoint(p1.lng_d - p0.lng_d, p1.lat_d - p0.lat_d);
	}

	/**
	 * 计算一些GPSElement覆盖的Grid，allowance是一个Grid容忍的误差，如allowance==1，一个Grid就要加上周围一圈8个Grid
	 * @param gpses
	 * @param allowance
	 * @return
	 */
	public Set<Grid> parse2Grids(Set<GPSElement> gpses, int allowance) {
		Set<Grid> ret = new TreeSet<>();
		for (GPSElement gps : gpses) {
			Set<Grid> grids = parse2Grids(gps);
			for (Grid grid : grids) {
				for (int ox = -allowance; ox <= allowance; ++ox) {
					for (int oy = -allowance; oy <= allowance; ++oy) {
						ret.add(grid.offset(ox, oy));
					}
				}
			}
		}
		return ret;
	}

	/**
	 * 同parse2Grids(gpses, 0)
	 * @param gpses
	 * @return
	 */
	public Set<Grid> parse2Grids(Set<GPSElement> gpses) {
		return parse2Grids(gpses, 0);
	}

	/**
	 * 同parse2Grids(new Set(gps), allowance)
	 * @param gps
	 * @param allowance
	 * @return
	 */
	public Set<Grid> parse2Grids(GPSElement gps, int allowance) {
		Set<Grid> ret = new TreeSet<>();
		Set<Grid> grids = parse2Grids(gps);
		for (Grid grid : grids) {
			for (int ox = -allowance; ox <= allowance; ++ox) {
				for (int oy = -allowance; oy <= allowance; ++oy) {
					ret.add(grid.offset(ox, oy));
				}
			}
		}
		return ret;
	}

	/**
	 * 将elements挂在一些Grid下
	 * @param grids
	 * @param elements
	 * @return this
	 */
	public GPSIndex<E> put(Set<Grid> grids, Set<E> elements) {
		if (elements == null || elements.size() == 0)
			return this;

		for (Grid grid : grids) {
			if (!map.containsKey(grid))
				map.put(grid, new TreeSet<E>());
			Set<E> set = map.get(grid);
			set.addAll(elements);
		}

		return this;
	}

	public GPSIndex<E> put(Set<Grid> grids, E element) {
		if (element == null)
			return this;

		for (Grid grid : grids) {
			if (!map.containsKey(grid))
				map.put(grid, new TreeSet<E>());
			Set<E> set = map.get(grid);
			set.add(element);
		}

		return this;
	}

	/**
	 * 获取Grids下的内容
	 * @param grids
	 * @return
	 */
	public Set<E> get(Set<Grid> grids) {
		Set<E> ret = new TreeSet<>();
		for (Grid grid : grids) {
			Set<E> set = map.get(grid);
			if (set != null && set.size() > 0)
				ret.addAll(set);
		}

		return ret;
	}

	/**
	 * 验证Grids下是否包含element
	 * @param grids
	 * @param element
	 * @return
	 */
	public boolean contains(Set<Grid> grids, E element) {
		if (element == null)
			return false;

		for (Grid grid : grids) {
			Set<E> set = map.get(grid);
			if (set != null && set.contains(element))
				return true;
		}

		return false;
	}

	/**
	 * 验证Grids下是否包含每一个element
	 * @param grids
	 * @param elements
	 * @return
	 */
	public boolean contains(Set<Grid> grids, Set<E> elements) {
		if (elements == null || elements.size() == 0)
			return false;

		for (Grid grid : grids) {
			Set<E> set = map.get(grid);
			if (set != null && set.containsAll(elements))
				return true;
		}

		return false;
	}

	/**
	 * 从grids移除element
	 * @param grids
	 * @param element
	 * @return this
	 */
	public GPSIndex<E> remove(Set<Grid> grids, E element) {
		if (element == null)
			return this;

		for (Grid grid : grids) {
			Set<E> set = map.get(grid);
			if (set != null) {
				set.remove(element);
				if (set.size() == 0)
					map.remove(grid);
			}
		}

		return this;
	}

	/**
	 * 从grids移除elements
	 * @param grids
	 * @param elements
	 * @return this
	 */
	public GPSIndex<E> remove(Set<Grid> grids, Set<E> elements) {
		if (elements == null || elements.size() == 0)
			return this;

		for (Grid grid : grids) {
			Set<E> set = map.get(grid);
			if (set != null) {
				set.removeAll(elements);
				if (set.size() == 0)
					map.remove(grid);
			}
		}

		return this;
	}

}
