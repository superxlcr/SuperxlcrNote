package fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.superxlcrnote.app.R;

/**
 * Created by Superxlcr
 * ø’ÀÈ∆¨£¨”√”⁄≤‚ ‘
 */
public class EmptyFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.empty_fragment, container, false);
	}
}
