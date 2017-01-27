package nl.han.wilkozonnenberg.festivalapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import nl.han.wilkozonnenberg.festivalapp.data.FestivalEvent;
import nl.han.wilkozonnenberg.festivalapp.database.FestivalProvider;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProgramFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProgramFragment#} factory method to
 * create an instance of this fragment.
 */
public class ProgramFragment extends Fragment {


    private Context mContext;
    private Cursor performances;
    public ProgramFragment() {
        // Required empty public constructor
    }

    private View rootView;
    private ProgramAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            setRetainInstance(true);

    }

    public void refresh(){
        refreshPerformances();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_program, container, false);
        final SwipeMenuListView listView = (SwipeMenuListView) rootView.findViewById(R.id.listview_Program);
        mContext = getContext();

        setAdapter(listView);

        refreshPerformances();


        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        listView.setMenuCreator(getSwipeMenu());

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {


                switch (index) {
                    case 0:
                        int oldPosition = performances.getPosition();
                        performances.moveToPosition(index);
                        int id = performances.getInt(performances.getColumnIndex("_id"));
                        getContext().getContentResolver().delete(FestivalProvider.USER_PROGRAM_CONTENT_URI,
                                "id = " + id, null);

                        performances.moveToPosition(oldPosition);
                        refreshPerformances();
                        setAdapter(listView);
                        adapter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });

        return rootView;
    }

    private void refreshPerformances(){

        performances = mContext.getContentResolver().query(FestivalProvider.USER_PROGRAM_CONTENT_URI,null,null,null,null);
        if(adapter == null){
            setAdapter((SwipeMenuListView) rootView.findViewById(R.id.listview_Program));
        }
        int i = performances.getCount();
        adapter = new
                ProgramAdapter(getActivity(), performances, false);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private SwipeMenuListView listView;

    public void setAdapter(SwipeMenuListView listView) {
        if(performances != null) {
            adapter = new
                    ProgramAdapter(getActivity(), performances, false);
            listView.setAdapter(adapter);
            this.listView = listView;
        }
    }

    private SwipeMenuCreator getSwipeMenu(){
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getContext());
                // set item background

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(180);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        return creator;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        performances = null;
        mContext = null;
        rootView = null;
        adapter = null;
    }
}
