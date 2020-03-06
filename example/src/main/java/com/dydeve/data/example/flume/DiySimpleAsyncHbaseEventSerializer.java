package com.dydeve.data.example.flume;

import com.google.common.base.Charsets;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.FlumeException;
import org.apache.flume.conf.ComponentConfiguration;
import org.apache.flume.sink.hbase.AsyncHbaseEventSerializer;
import org.apache.flume.sink.hbase.SimpleHbaseEventSerializer;
import org.apache.flume.sink.hbase.SimpleRowKeyGenerator;
import org.hbase.async.AtomicIncrementRequest;
import org.hbase.async.PutRequest;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Date 下午10:04 2020/2/10
 * @Author: joker
 */
public class DiySimpleAsyncHbaseEventSerializer implements AsyncHbaseEventSerializer {
	private byte[] table;
	private byte[] cf;
	private byte[] payload;
	private byte[] payloadColumn;//在properties里配置
	private byte[] incrementColumn;
	private String rowPrefix;
	private byte[] incrementRow;
	private SimpleHbaseEventSerializer.KeyType keyType;

	@Override
	public void initialize(byte[] table, byte[] cf) {
		this.table = table;
		this.cf = cf;
	}

	@Override
	public List<PutRequest> getActions() {
		List<PutRequest> actions = new ArrayList<PutRequest>();
		if (payloadColumn != null) {
			String[] qualifiers = new String(payloadColumn).split(",");
			String[] values = new String(payload).split(",");
			if (qualifiers.length != values.length) {
				return actions;
			}

			String startTime = values[6];
			String uid = values[2];
			String taskId = values[0];

			String rowKey = uid + startTime + taskId;

			for (int i = 0; i < qualifiers.length; i++) {
				PutRequest putRequest =  new PutRequest(table, rowKey.getBytes(StandardCharsets.UTF_8), cf,
						qualifiers[i].getBytes(StandardCharsets.UTF_8), values[i].getBytes(StandardCharsets.UTF_8));
				actions.add(putRequest);
			}

				/*switch (keyType) {
					case TS:
						rowKey = SimpleRowKeyGenerator.getTimestampKey(rowPrefix);
						break;
					case TSNANO:
						rowKey = SimpleRowKeyGenerator.getNanoTimestampKey(rowPrefix);
						break;
					case RANDOM:
						rowKey = SimpleRowKeyGenerator.getRandomKey(rowPrefix);
						break;
					default:
						rowKey = SimpleRowKeyGenerator.getUUIDKey(rowPrefix);
						break;
				}*/
		}
		return actions;
	}

	public List<AtomicIncrementRequest> getIncrements() {
		List<AtomicIncrementRequest> actions = new ArrayList<AtomicIncrementRequest>();
		if (incrementColumn != null) {
			AtomicIncrementRequest inc = new AtomicIncrementRequest(table,
					incrementRow, cf, incrementColumn);
			actions.add(inc);
		}
		return actions;
	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub

	}

	@Override
	public void configure(Context context) {
		String pCol = context.getString("payloadColumn", "pCol");
		String iCol = context.getString("incrementColumn", "iCol");
		rowPrefix = context.getString("rowPrefix", "default");
		String suffix = context.getString("suffix", "uuid");
		if (pCol != null && !pCol.isEmpty()) {
			if (suffix.equals("timestamp")) {
				keyType = SimpleHbaseEventSerializer.KeyType.TS;
			} else if (suffix.equals("random")) {
				keyType = SimpleHbaseEventSerializer.KeyType.RANDOM;
			} else if (suffix.equals("nano")) {
				keyType = SimpleHbaseEventSerializer.KeyType.TSNANO;
			} else {
				keyType = SimpleHbaseEventSerializer.KeyType.UUID;
			}
			payloadColumn = pCol.getBytes(Charsets.UTF_8);
		}
		if (iCol != null && !iCol.isEmpty()) {
			incrementColumn = iCol.getBytes(Charsets.UTF_8);
		}
		incrementRow = context.getString("incrementRow", "incRow").getBytes(Charsets.UTF_8);
	}

	@Override
	public void setEvent(Event event) {
		this.payload = event.getBody();
	}

	@Override
	public void configure(ComponentConfiguration conf) {
		// TODO Auto-generated method stub
	}

}