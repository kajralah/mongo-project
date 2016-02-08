package model;

public class WrittenMessages {
	
	private String _id;
	private int count;
	
	public WrittenMessages() {
	}

	public WrittenMessages(String _id, int count) {
		super();
		this._id = _id;
		this.count = count;
	}

	public String getId() {
		return _id;
	}

	public int getCount() {
		return count;
	}

	@Override
	public String toString() {
		return "WrittenMessages [id=" + _id + ", count=" + count + "]";
	}

}
