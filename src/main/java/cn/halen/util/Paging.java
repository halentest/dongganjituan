package cn.halen.util;

public class Paging {
	private static final int PAGE_SIZE = 20;
	
	private int start;
	
	private long totalCount;
	
	public long getTotalCount() {
		return totalCount;
	}

	private long pageCount;
	
	public long getPageCount() {
		return pageCount;
	}

	private int page;
	
	public int getPageSize() {
		return pageSize;
	}

	private int pageSize;
	
	public Paging(int page, long totalCount) {
		this(page, PAGE_SIZE, totalCount);
	}
	
	public Paging(int page, int pageSize, long totalCount) {
		if(!(page>0)) {
			throw new IllegalArgumentException("page must larger than 0");
		}
		this.totalCount = totalCount;
		this.pageCount = (totalCount % pageSize) == 0? totalCount/pageSize : totalCount/pageSize + 1;
		if(page <= pageCount) {
			this.page = page;
		} else {
			this.page = (int) pageCount;
		}
		this.pageSize = pageSize;
		this.start = pageSize * (this.page - 1);
	}
	
	public int getStart() {
		return start;
	}
	
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getSize() {
		return pageSize;
	}
}
